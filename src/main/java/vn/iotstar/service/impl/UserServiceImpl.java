package vn.iotstar.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.impl.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.util.EmailUtil;
import vn.iotstar.util.OtpUtil;
import vn.iotstar.util.PasswordUtil;

public class UserServiceImpl implements IUserService {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final String PURPOSE_ACTIVATE = "ACTIVATE";
    private static final String PURPOSE_RESET = "RESET";

    /** OTP có hiệu lực 5 phút, được nhập sai tối đa 5 lần, gửi lại cách nhau tối thiểu 30 giây. */
    private static final int OTP_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_SECONDS = 30;

    private final IUserDao userDao = new UserDao();

    // ------------------------------------------------------------------ đăng ký / đăng nhập

    @Override
    public User register(String username, String password, String fullname, String email) {
        username = username == null ? "" : username.trim();
        fullname = fullname == null ? "" : fullname.trim();
        email = normalizeEmail(email);

        if (!USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException("Tên đăng nhập gồm 3-30 ký tự: chữ không dấu, số hoặc dấu gạch dưới");
        }
        if (fullname.isEmpty() || fullname.length() > 100) {
            throw new IllegalArgumentException("Họ tên không được để trống (tối đa 100 ký tự)");
        }
        if (!EMAIL.matcher(email).matches() || email.length() > 100) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Tài khoản đăng ký nhưng chưa kích hoạt OTP thì không "giữ chỗ" username / email:
        // xoá bản cũ để người dùng đăng ký lại được.
        User byName = userDao.findByUsername(username);
        if (byName != null) {
            if (byName.isActive()) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
            }
            userDao.delete(byName.getUserId());
        }
        User byMail = userDao.findByEmail(email);
        if (byMail != null) {
            if (byMail.isActive()) {
                throw new IllegalArgumentException("Email đã được sử dụng");
            }
            userDao.delete(byMail.getUserId());
        }

        User user = new User();
        user.setUsername(username);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash(password));
        user.setActive(false);
        userDao.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userDao.findByUsername(username.trim());
        if (user != null && PasswordUtil.verify(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // ------------------------------------------------------------------ kích hoạt tài khoản

    @Override
    public void sendActivationOtp(String email) {
        User user = userDao.findByEmail(normalizeEmail(email));
        if (user == null || user.isActive()) {
            throw new IllegalArgumentException("Tài khoản không tồn tại hoặc đã được kích hoạt");
        }
        issueOtp(user, PURPOSE_ACTIVATE, "kích hoạt tài khoản");
    }

    @Override
    public void activate(String email, String otp) {
        User user = userDao.findByEmail(normalizeEmail(email));
        if (user == null || user.isActive()) {
            throw new IllegalArgumentException("Tài khoản không tồn tại hoặc đã được kích hoạt");
        }
        checkOtp(user, PURPOSE_ACTIVATE, otp);
        user.setActive(true);
        clearOtp(user);
        userDao.update(user);
    }

    // ------------------------------------------------------------------ quên mật khẩu

    @Override
    public void requestPasswordReset(String email) {
        String mail = normalizeEmail(email);
        if (!EMAIL.matcher(mail).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        User user = userDao.findByEmail(mail);
        if (user == null || !user.isActive()) {
            return; // không tiết lộ email có tồn tại hay không
        }
        issueOtp(user, PURPOSE_RESET, "đặt lại mật khẩu");
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        User user = userDao.findByEmail(normalizeEmail(email));
        if (user == null) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ hoặc đã hết hạn");
        }
        checkOtp(user, PURPOSE_RESET, otp);
        user.setPassword(PasswordUtil.hash(newPassword));
        clearOtp(user);
        userDao.update(user);
    }

    // ------------------------------------------------------------------ OTP dùng chung

    private void issueOtp(User user, String purpose, String actionText) {
        LocalDateTime now = LocalDateTime.now();
        if (user.getOtpExpiry() != null && user.getOtp() != null) {
            LocalDateTime issuedAt = user.getOtpExpiry().minusMinutes(OTP_MINUTES);
            long wait = RESEND_SECONDS - Duration.between(issuedAt, now).getSeconds();
            if (wait > 0) {
                throw new IllegalArgumentException("Vui lòng đợi " + wait + " giây nữa rồi hãy gửi lại mã");
            }
        }
        String otp = OtpUtil.generate();
        user.setOtp(otp);
        user.setOtpPurpose(purpose);
        user.setOtpExpiry(now.plusMinutes(OTP_MINUTES));
        user.setOtpAttempts(0);
        userDao.update(user);
        EmailUtil.sendOtp(user.getEmail(), user.getFullname(), otp, actionText, OTP_MINUTES);
    }

    private void checkOtp(User user, String purpose, String input) {
        if (user.getOtp() == null || !purpose.equals(user.getOtpPurpose())) {
            throw new IllegalArgumentException("Chưa có mã OTP hợp lệ. Hãy bấm \"Gửi lại mã\"");
        }
        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn. Hãy bấm \"Gửi lại mã\"");
        }
        if (user.getOtpAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Bạn đã nhập sai quá " + MAX_ATTEMPTS + " lần. Hãy bấm \"Gửi lại mã\"");
        }
        String code = input == null ? "" : input.trim();
        boolean ok = MessageDigest.isEqual(user.getOtp().getBytes(StandardCharsets.UTF_8),
                code.getBytes(StandardCharsets.UTF_8));
        if (!ok) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            userDao.update(user);
            int left = MAX_ATTEMPTS - user.getOtpAttempts();
            throw new IllegalArgumentException("Mã OTP không đúng" + (left > 0 ? " (còn " + left + " lần thử)" : ""));
        }
    }

    private void clearOtp(User user) {
        user.setOtp(null);
        user.setOtpPurpose(null);
        user.setOtpExpiry(null);
        user.setOtpAttempts(0);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
