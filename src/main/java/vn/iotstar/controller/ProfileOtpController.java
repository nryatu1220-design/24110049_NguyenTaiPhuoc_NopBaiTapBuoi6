package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;

@WebServlet("/profile/verify-otp")
public class ProfileOtpController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String email = session == null
                ? null : (String) session.getAttribute(ProfileController.SESSION_PENDING_EMAIL);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        Object flash = session.getAttribute("profileOtpFlash");
        if (flash != null) {
            req.setAttribute("info", flash);
            session.removeAttribute("profileOtpFlash");
        }
        req.setAttribute("maskedEmail", mask(email));
        req.getRequestDispatcher("/views/profile-verify-otp.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Integer userId = (Integer) session.getAttribute("userId");
        String email = (String) session.getAttribute(ProfileController.SESSION_PENDING_EMAIL);
        String fullname = (String) session.getAttribute(ProfileController.SESSION_PENDING_FULLNAME);
        String phone = (String) session.getAttribute(ProfileController.SESSION_PENDING_PHONE);
        String avatar = (String) session.getAttribute(ProfileController.SESSION_PENDING_AVATAR);
        if (userId == null || email == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        try {
            if ("resend".equals(req.getParameter("action"))) {
                userService.requestProfileUpdate(userId, fullname, phone, email);
                req.setAttribute("info", "Đã gửi lại mã OTP đến email mới.");
            } else {
                userService.confirmProfileUpdate(userId, email, fullname, phone, avatar,
                        req.getParameter("otp"));
                clearPending(session);
                session.setAttribute("fullname", fullname);
                session.setAttribute("profileFlash", "Xác nhận OTP thành công. Hồ sơ đã được cập nhật.");
                resp.sendRedirect(req.getContextPath() + "/profile");
                return;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.setAttribute("maskedEmail", mask(email));
        req.getRequestDispatcher("/views/profile-verify-otp.jsp").include(req, resp);
    }

    private static void clearPending(HttpSession session) {
        session.removeAttribute(ProfileController.SESSION_PENDING_FULLNAME);
        session.removeAttribute(ProfileController.SESSION_PENDING_PHONE);
        session.removeAttribute(ProfileController.SESSION_PENDING_EMAIL);
        session.removeAttribute(ProfileController.SESSION_PENDING_AVATAR);
    }

    private static String mask(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return email;
        return email.charAt(0) + "****" + email.substring(at - 1);
    }
}
