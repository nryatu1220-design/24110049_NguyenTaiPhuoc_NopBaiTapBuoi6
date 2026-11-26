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

@WebServlet("/admin/user/edit")
public class UserEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final IUserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = findUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/list");
            return;
        }
        req.setAttribute("user", user);
        req.getRequestDispatcher("/views/admin/edit-user.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = findUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/list");
            return;
        }
        String fullname = trim(req.getParameter("fullname"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String phone = trim(req.getParameter("phone"));
        String error = null;
        if (fullname.isEmpty() || fullname.length() > 100) {
            error = "Họ tên không được để trống (tối đa 100 ký tự).";
        } else if (!EMAIL.matcher(email).matches() || email.length() > 100) {
            error = "Email không hợp lệ.";
        } else if (phone.length() > 20) {
            error = "Số điện thoại tối đa 20 ký tự.";
        } else {
            User existing = userDao.findByEmail(email);
            if (existing != null && existing.getUserId() != user.getUserId()) {
                error = "Email đã được sử dụng.";
            }
        }
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setActive("1".equals(req.getParameter("active")));
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("user", user);
            req.getRequestDispatcher("/views/admin/edit-user.jsp").include(req, resp);
            return;
        }
        userDao.update(user);
        resp.sendRedirect(req.getContextPath() + "/admin/user/list");
    }

    private User findUser(HttpServletRequest req) {
        try {
            return userDao.findById(Integer.parseInt(req.getParameter("id")));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
