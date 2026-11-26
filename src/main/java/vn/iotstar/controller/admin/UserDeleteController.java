package vn.iotstar.controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.impl.UserDao;

@WebServlet("/admin/user/delete")
public class UserDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            HttpSession session = req.getSession(false);
            Object currentUserId = session == null ? null : session.getAttribute("userId");
            if (!(currentUserId instanceof Integer) || ((Integer) currentUserId) != userId) {
                userDao.delete(userId);
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs and return to the list.
        }
        resp.sendRedirect(req.getContextPath() + "/admin/user/list");
    }
}
