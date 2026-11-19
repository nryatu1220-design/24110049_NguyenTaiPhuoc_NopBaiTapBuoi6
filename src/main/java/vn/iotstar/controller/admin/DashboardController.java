package vn.iotstar.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;

@WebServlet(urlPatterns = { "/admin/dashboard" })
public class DashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ICategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        if (categories == null) categories = new ArrayList<>();

        int imageCount = 0;
        for (Category category : categories) {
            if (category.getImages() != null && !category.getImages().trim().isEmpty()) imageCount++;
        }

        // Danh mục có ID lớn hơn được xem là tạo sau.
        List<Category> recentCategories = new ArrayList<>(categories);
        recentCategories.sort((a, b) -> Integer.compare(b.getCategoryId(), a.getCategoryId()));
        if (recentCategories.size() > 5)
            recentCategories = new ArrayList<>(recentCategories.subList(0, 5));

        req.setAttribute("categoryCount", categories.size());
        req.setAttribute("imageCount", imageCount);
        req.setAttribute("recentCategories", recentCategories);
        req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);
    }
}
