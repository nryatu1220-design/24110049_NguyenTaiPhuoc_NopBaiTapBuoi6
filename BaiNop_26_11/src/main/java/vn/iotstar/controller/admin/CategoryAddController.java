package vn.iotstar.controller.admin;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FileUploadUtil;

@WebServlet(urlPatterns = { "/admin/category/add" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class CategoryAddController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp");

    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/admin/add-category.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String name = req.getParameter("catename");
        name = name == null ? "" : name.trim();
        if (name.isEmpty() || name.length() > 255) {
            req.setAttribute("error", name.isEmpty()
                    ? "Tên danh mục không được để trống"
                    : "Tên danh mục tối đa 255 ký tự");
            Category invalidCategory = new Category();
            invalidCategory.setCategoryname(name);
            invalidCategory.setStatus("0".equals(req.getParameter("status")) ? 0 : 1);
            req.setAttribute("category", invalidCategory);
            req.getRequestDispatcher("/views/admin/add-category.jsp").include(req, resp);
            return;
        }

        Category category = new Category();
        category.setCategoryname(name);
        category.setStatus("0".equals(req.getParameter("status")) ? 0 : 1);

        String uploadedImage;
        try {
            uploadedImage = FileUploadUtil.saveFile(req.getPart("icon"), Constant.DIR, IMAGE_EXT);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/add-category.jsp").include(req, resp);
            return;
        } catch (IOException | ServletException | IllegalStateException e) {
            req.setAttribute("error", "Không thể tải ảnh lên. Vui lòng kiểm tra định dạng và dung lượng.");
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/add-category.jsp").include(req, resp);
            return;
        }
        category.setImages(uploadedImage);

        try {
            cateService.insert(category);
        } catch (IllegalArgumentException e) {
            FileUploadUtil.deleteQuietly(Constant.DIR, uploadedImage);
            req.setAttribute("error", e.getMessage());
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/add-category.jsp").include(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/category/list");
    }
}
