package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CloudinaryUtil;

@WebServlet("/profile")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String SESSION_PENDING_FULLNAME = "profilePendingFullname";
    public static final String SESSION_PENDING_PHONE = "profilePendingPhone";
    public static final String SESSION_PENDING_EMAIL = "profilePendingEmail";
    public static final String SESSION_PENDING_AVATAR = "profilePendingAvatar";

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Integer userId = session == null ? null : (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = new vn.iotstar.dao.impl.UserDao().findById(userId);
        if (user == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (session.getAttribute("profileFlash") != null) {
            req.setAttribute("profileFlash", session.getAttribute("profileFlash"));
            session.removeAttribute("profileFlash");
        }
        req.setAttribute("profile", user);
        req.getRequestDispatcher("/views/profile.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Integer userId = session == null ? null : (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User current = new vn.iotstar.dao.impl.UserDao().findById(userId);
        if (current == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        try {
            String avatarUrl = CloudinaryUtil.uploadProfileImage(req.getPart("avatar"));
            if (avatarUrl == null) {
                avatarUrl = current.getAvatarUrl();
            }

            userService.requestProfileUpdate(userId, fullname, phone, normalizedEmail);
            session.setAttribute(SESSION_PENDING_FULLNAME,
                    fullname == null ? "" : fullname.trim());
            session.setAttribute(SESSION_PENDING_PHONE,
                    phone == null ? "" : phone.trim());
            session.setAttribute(SESSION_PENDING_EMAIL, normalizedEmail);
            session.setAttribute(SESSION_PENDING_AVATAR, avatarUrl);
            String recipient = normalizedEmail.equalsIgnoreCase(current.getEmail())
                    ? "email hiện tại" : "email mới";
            session.setAttribute("profileOtpFlash",
                    "Mã OTP đã được gửi đến " + recipient + " để xác nhận cập nhật.");
            resp.sendRedirect(req.getContextPath() + "/profile/verify-otp");
        } catch (IllegalArgumentException | IllegalStateException | ServletException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("profile", current);
            req.getRequestDispatcher("/views/profile.jsp").include(req, resp);
        }
    }
}
