package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String fullname = req.getParameter("fullname");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");

        User user;
        try {
            if (password == null || !password.equals(confirm)) {
                throw new IllegalArgumentException("Mật khẩu nhập lại không khớp");
            }
            user = userService.register(username, password, fullname, email);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            // Giữ lại dữ liệu đã nhập (trừ mật khẩu)
            req.setAttribute("username", username);
            req.setAttribute("fullname", fullname);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
            return;
        }

        // Tài khoản đã tạo (chưa kích hoạt) -> gửi OTP rồi chuyển sang trang nhập mã
        HttpSession session = req.getSession(true);
        session.setAttribute(VerifyOtpController.SESSION_EMAIL, user.getEmail());
        try {
            userService.sendActivationOtp(user.getEmail());
            session.setAttribute("otpFlash", "Chúng tôi đã gửi mã OTP đến email của bạn.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Gửi mail lỗi: vẫn cho sang trang xác nhận để bấm "Gửi lại mã"
            session.setAttribute("otpFlashError", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/verify-otp");
    }
}
