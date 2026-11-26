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
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        int total = normalizedKeyword.isEmpty()
                ? cateService.count()
                : cateService.countByName(normalizedKeyword);
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        int page = 1;
        try {
            page = Integer.parseInt(req.getParameter("page"));
        } catch (NumberFormatException e) {
            // Missing or malformed page uses the first page.
        }
        page = Math.min(Math.max(page, 1), totalPages);

        List<Category> cateList = normalizedKeyword.isEmpty()
                ? cateService.findAll(page - 1, PAGE_SIZE)
                : cateService.searchByName(normalizedKeyword, page - 1, PAGE_SIZE);

        req.setAttribute("cateList", cateList);
        req.setAttribute("keyword", keyword);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("offset", (page - 1) * PAGE_SIZE);
        req.getRequestDispatcher("/views/admin/list-category.jsp").include(req, resp);
    }
}
