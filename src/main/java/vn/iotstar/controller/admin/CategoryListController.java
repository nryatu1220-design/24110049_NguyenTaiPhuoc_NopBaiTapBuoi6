package vn.iotstar.controller.admin;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;

@WebServlet(urlPatterns = { "/admin/category/list" })
public class CategoryListController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 5;

    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String keyword = req.getParameter("keyword");
        List<Category> cateList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm: hiển thị toàn bộ kết quả, không phân trang
            cateList = cateService.searchByName(keyword.trim());
        } else {
            int totalPages = Math.max(1, (int) Math.ceil(cateService.count() / (double) PAGE_SIZE));
            int page = 1;
            try {
                page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) {
                // không có / sai page -> trang 1
            }
            page = Math.min(Math.max(page, 1), totalPages);

            cateList = cateService.findAll(page - 1, PAGE_SIZE);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("offset", (page - 1) * PAGE_SIZE);
        }

        req.setAttribute("cateList", cateList);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/views/admin/list-category.jsp").forward(req, resp);
    }
}
