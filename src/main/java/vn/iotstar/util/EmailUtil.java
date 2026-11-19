package vn.iotstar.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Gửi email OTP qua SMTP. Có thể ghi đè cấu hình mặc định bằng
 * ~/.shopping-mail.properties hoặc biến môi trường MAIL_USERNAME / MAIL_PASSWORD.
 *
 * Nếu chưa cấu hình tài khoản email, mã OTP được in ra console của Tomcat
 * (chế độ DEV) để vẫn chạy thử được toàn bộ luồng.
 */
public class EmailUtil {

    private static final Properties CONFIG = load();

    private EmailUtil() {
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = EmailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                p.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Không đọc được cấu hình email mặc định.", e);
        }

        Path localConfig = Path.of(System.getProperty("user.home"), ".shopping-mail.properties");
        if (Files.isRegularFile(localConfig)) {
            try (InputStream in = Files.newInputStream(localConfig)) {
                p.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (java.io.IOException e) {
                throw new IllegalStateException("Không đọc được cấu hình email local: " + localConfig, e);
            }
        }

        String envUser = System.getenv("MAIL_USERNAME");
        String envPass = System.getenv("MAIL_PASSWORD");
        if (envUser != null && !envUser.isBlank()) {
            p.setProperty("mail.username", envUser);
        }
        if (envPass != null && !envPass.isBlank()) {
            p.setProperty("mail.password", envPass);
        }
        return p;
    }

    private static boolean isConfigured() {
        String user = CONFIG.getProperty("mail.username", "").trim();
        String pass = CONFIG.getProperty("mail.password", "").trim();
        return !user.isEmpty() && !user.startsWith("your_")
                && !pass.isEmpty() && !pass.startsWith("your_");
    }

    /**
     * Gửi mã OTP.
     *
     * @param action nội dung hành động, ví dụ "kích hoạt tài khoản" hoặc "đặt lại mật khẩu"
     * @throws IllegalStateException nếu gửi email thất bại
     */
    public static void sendOtp(String to, String fullname, String otp, String action, int minutes) {
        if (!isConfigured()) {
            System.out.println("[EmailUtil][DEV] Chưa cấu hình mail.properties -> không gửi email thật.");
            System.out.println("[EmailUtil][DEV] OTP " + action + " cho " + to + " = " + otp
                    + " (hiệu lực " + minutes + " phút)");
            return;
        }

        final String username = CONFIG.getProperty("mail.username").trim();
        final String password = CONFIG.getProperty("mail.password").trim();
        String from = CONFIG.getProperty("mail.from", "").trim();
        if (from.isEmpty() || from.startsWith("your_")) {
            from = username;
        }
        String fromName = CONFIG.getProperty("mail.from.name", "Shopping");

        // Chuyển mọi khoá mail.smtp.* trong file cấu hình sang Properties của Session
        Properties props = new Properties();
        for (String key : CONFIG.stringPropertyNames()) {
            if (key.startsWith("mail.smtp.")) {
                props.setProperty(key, CONFIG.getProperty(key).trim());
            }
        }
        props.putIfAbsent("mail.smtp.host", "smtp.gmail.com");
        props.putIfAbsent("mail.smtp.port", "587");
        props.putIfAbsent("mail.smtp.auth", "true");
        props.putIfAbsent("mail.smtp.starttls.enable", "true");
        props.putIfAbsent("mail.smtp.connectiontimeout", "10000");
        props.putIfAbsent("mail.smtp.timeout", "10000");
        props.putIfAbsent("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject("Mã OTP " + action + ": " + otp, "UTF-8");
            msg.setContent(buildHtml(fullname, otp, action, minutes), "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Không gửi được email. Vui lòng thử lại sau.", e);
        }
    }

    private static String buildHtml(String fullname, String otp, String action, int minutes) {
        return "<div style=\"font-family:Segoe UI,Arial,sans-serif;max-width:480px;margin:auto;padding:24px;"
                + "border:1px solid #e1dfdd\">"
                + "<h2 style=\"margin:0 0 12px;color:#0078d4\">Shopping</h2>"
                + "<p>Xin chào <b>" + escape(fullname) + "</b>,</p>"
                + "<p>Mã OTP để " + escape(action) + " của bạn là:</p>"
                + "<p style=\"font-size:32px;letter-spacing:8px;font-weight:700;margin:16px 0\">" + otp + "</p>"
                + "<p>Mã có hiệu lực trong <b>" + minutes + " phút</b>. Tuyệt đối không chia sẻ mã này cho người khác.</p>"
                + "<p style=\"color:#605e5c;font-size:12px\">Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.</p>"
                + "</div>";
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
