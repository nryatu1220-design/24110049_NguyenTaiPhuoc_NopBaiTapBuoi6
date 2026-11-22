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

@WebServlet(urlPatterns = { "/admin/category/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class CategoryEditController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp");

    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Category category = null;
        try {
            category = cateService.findById(Integer.parseInt(req.getParameter("id")));
        } catch (NumberFormatException e) {
            // id sai -> quay về danh sách
        }
        if (category == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        req.setAttribute("category", category);
        req.getRequestDispatcher("/views/admin/edit-category.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        Category category = new Category();
        category.setCategoryId(id);
        String name = req.getParameter("catename");
        category.setCategoryname(name == null ? "" : name.trim());
        category.setStatus("0".equals(req.getParameter("status")) ? 0 : 1);

        Category currentCategory = cateService.findById(id);
        if (currentCategory == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }
        category.setImages(currentCategory.getImages());
        if (category.getCategoryname().isEmpty() || category.getCategoryname().length() > 255) {
            req.setAttribute("error", category.getCategoryname().isEmpty()
                    ? "Tên danh mục không được để trống"
                    : "Tên danh mục tối đa 255 ký tự");
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/edit-category.jsp").include(req, resp);
            return;
        }

        String uploadedImage;
        try {
            uploadedImage = FileUploadUtil.saveFile(req.getPart("icon"), Constant.DIR, IMAGE_EXT);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/edit-category.jsp").include(req, resp);
            return;
        } catch (IOException | ServletException | IllegalStateException e) {
            req.setAttribute("error", "Không thể tải ảnh lên. Vui lòng kiểm tra định dạng và dung lượng.");
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/edit-category.jsp").include(req, resp);
            return;
        }
        if (uploadedImage != null) {
            category.setImages(uploadedImage);
        }

        try {
            cateService.update(category);
        } catch (IllegalArgumentException e) {
            FileUploadUtil.deleteQuietly(Constant.DIR, uploadedImage);
            category.setImages(currentCategory.getImages());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/edit-category.jsp").include(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/category/list");
    }
}
