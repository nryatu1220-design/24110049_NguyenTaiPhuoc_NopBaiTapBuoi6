package vn.iotstar.controller.video;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;

/** /video?page=1 : tất cả video của user đang đăng nhập, 6 video / trang. */
@WebServlet(urlPatterns = { "/video" })
public class VideoListController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int PAGE_SIZE = 6;

    private final IVideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");

        long total = videoService.countByUser(userId);
        int totalPages = (int) Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);

        int page = 1;
        try {
            page = Integer.parseInt(req.getParameter("page"));
        } catch (NumberFormatException ignored) {
            // không có / sai tham số -> trang 1
        }
        page = Math.min(Math.max(page, 1), totalPages);

        req.setAttribute("videos", videoService.findByUser(userId, page, PAGE_SIZE));
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("total", total);

        String msg = req.getParameter("msg");
        if ("added".equals(msg)) {
            req.setAttribute("success", "Đã thêm video.");
        } else if ("updated".equals(msg)) {
            req.setAttribute("success", "Đã cập nhật video.");
        } else if ("deleted".equals(msg)) {
            req.setAttribute("success", "Đã xoá video.");
        } else if ("notfound".equals(msg)) {
            req.setAttribute("error", "Không tìm thấy video hoặc bạn không có quyền thao tác.");
        }
        req.getRequestDispatcher("/views/web/video-list.jsp").forward(req, resp);
    }
}
