package vn.iotstar.controller.admin;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.impl.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.util.PasswordUtil;

@WebServlet("/admin/user/add")
public class UserAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final IUserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/admin/add-user.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = trim(req.getParameter("username"));
        String fullname = trim(req.getParameter("fullname"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String phone = trim(req.getParameter("phone"));
        String password = req.getParameter("password");
        String error = null;
        if (!USERNAME.matcher(username).matches()) {
            error = "Tên đăng nhập gồm 3-30 ký tự: chữ, số hoặc dấu gạch dưới.";
        } else if (fullname.isEmpty() || fullname.length() > 100) {
            error = "Họ tên không được để trống (tối đa 100 ký tự).";
        } else if (!EMAIL.matcher(email).matches() || email.length() > 100) {
            error = "Email không hợp lệ.";
        } else if (phone.length() > 20) {
            error = "Số điện thoại tối đa 20 ký tự.";
        } else if (password == null || password.length() < 6) {
            error = "Mật khẩu phải có ít nhất 6 ký tự.";
        } else if (userDao.findByUsername(username) != null || userDao.findByEmail(email) != null) {
            error = "Tên đăng nhập hoặc email đã được sử dụng.";
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("username", username);
            req.setAttribute("fullname", fullname);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            req.setAttribute("active", "1".equals(req.getParameter("active")));
            req.getRequestDispatcher("/views/admin/add-user.jsp").include(req, resp);
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(PasswordUtil.hash(password));
        user.setActive("1".equals(req.getParameter("active")));
        userDao.insert(user);
        resp.sendRedirect(req.getContextPath() + "/admin/user/list");
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
