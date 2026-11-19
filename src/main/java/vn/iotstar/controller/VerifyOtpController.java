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

/** Trang nhập OTP để kích hoạt tài khoản sau khi đăng ký (hoặc sau khi đăng nhập tài khoản chưa kích hoạt). */
@WebServlet("/verify-otp")
public class VerifyOtpController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Email đang chờ kích hoạt, lưu trong session. */
    public static final String SESSION_EMAIL = "otpEmail";

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute(SESSION_EMAIL);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // Thông báo một lần (flash) do Register / Login đặt vào session
        moveFlash(session, "otpFlash", req, "info");
        moveFlash(session, "otpFlashError", req, "error");
        req.setAttribute("maskedEmail", mask(email));
        req.getRequestDispatcher("/views/verify-otp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute(SESSION_EMAIL);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("resend".equals(req.getParameter("action"))) {
            try {
                userService.sendActivationOtp(email);
                req.setAttribute("info", "Đã gửi lại mã OTP. Vui lòng kiểm tra email.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                req.setAttribute("error", e.getMessage());
            }
        } else {
            try {
                userService.activate(email, req.getParameter("otp"));
                session.removeAttribute(SESSION_EMAIL);
                resp.sendRedirect(req.getContextPath() + "/login?activated=1");
                return;
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", e.getMessage());
            }
        }
        req.setAttribute("maskedEmail", mask(email));
        req.getRequestDispatcher("/views/verify-otp.jsp").forward(req, resp);
    }

    private static void moveFlash(HttpSession session, String key, HttpServletRequest req, String attr) {
        Object v = session.getAttribute(key);
        if (v != null) {
            req.setAttribute(attr, v);
            session.removeAttribute(key);
        }
    }

    /** abc123@gmail.com -> a****3@gmail.com */
    static String mask(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return email;
        }
        String name = email.substring(0, at);
        return name.charAt(0) + "****" + name.charAt(name.length() - 1) + email.substring(at);
    }
}
