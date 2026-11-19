package vn.iotstar.controller.video;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FileUploadUtil;

/**
 * Thêm / sửa video bằng form multipart (upload poster + file video).
 * /video/add  : thêm mới
 * /video/edit?id=... : sửa
 */
@WebServlet(urlPatterns = { "/video/add", "/video/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,        // > 1MB thì ghi tạm ra đĩa
        maxFileSize = 1024 * 1024 * 200,                 // mỗi file tối đa 200MB
        maxRequestSize = 1024 * 1024 * 210)              // cả request tối đa 210MB
public class VideoSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> VIDEO_EXT = Set.of("mp4", "webm", "ogg", "mov");
    private static final String FORM = "/views/web/video-form.jsp";

    private final IVideoService videoService = new VideoServiceImpl();

    private static boolean isEdit(HttpServletRequest req) {
        return req.getServletPath().endsWith("/edit");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (isEdit(req)) {
            int userId = (Integer) req.getSession().getAttribute("userId");
            Video video = videoService.findOwned(req.getParameter("id"), userId);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video?msg=notfound");
                return;
            }
            req.setAttribute("video", video);
        }
        req.setAttribute("editMode", isEdit(req));
        req.getRequestDispatcher(FORM).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        boolean edit = isEdit(req);
        int userId = (Integer) req.getSession().getAttribute("userId");

        Video video;
        if (edit) {
            // Lấy video hiện có (đã kiểm tra quyền sở hữu) rồi ghi đè các trường được sửa
            video = videoService.findOwned(req.getParameter("id"), userId);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video?msg=notfound");
                return;
            }
        } else {
            video = new Video();
        }

        String oldPoster = video.getPoster();
        String oldVideoFile = video.getVideoFile();
        String newPoster = null;
        String newVideoFile = null;

        try {
            // Gọi getPart trước: nếu file vượt giới hạn @MultipartConfig sẽ ném lỗi tại đây
            Part posterPart = req.getPart("poster");
            Part videoPart = req.getPart("videoFile");

            video.setTitle(req.getParameter("title"));
            String desc = req.getParameter("description");
            video.setDescription(desc == null || desc.isBlank() ? null : desc.trim());
            video.setActive(req.getParameter("active") != null);

            newPoster = FileUploadUtil.saveFile(posterPart, Constant.VIDEO_DIR, IMAGE_EXT);
            newVideoFile = FileUploadUtil.saveFile(videoPart, Constant.VIDEO_DIR, VIDEO_EXT);
            if (newPoster != null) {
                video.setPoster(newPoster);
            }
            if (newVideoFile != null) {
                video.setVideoFile(newVideoFile);
            }

            if (edit) {
                videoService.update(video, userId);
            } else {
                videoService.insert(video, userId);
            }
        } catch (IllegalArgumentException | IllegalStateException | ServletException e) {
            // Lưu thất bại: xoá file vừa upload để khỏi rác
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, newPoster);
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, newVideoFile);
            video.setPoster(oldPoster);
            video.setVideoFile(oldVideoFile);

            String message = e instanceof IllegalArgumentException ? e.getMessage()
                    : "Upload thất bại. File quá lớn (tối đa 200MB) hoặc dữ liệu không hợp lệ.";
            req.setAttribute("error", message);
            req.setAttribute("video", video);
            req.setAttribute("editMode", edit);
            req.getRequestDispatcher(FORM).forward(req, resp);
            return;
        }

        // Thành công: xoá file cũ đã bị thay thế
        if (newPoster != null) {
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, oldPoster);
        }
        if (newVideoFile != null) {
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, oldVideoFile);
        }
        resp.sendRedirect(req.getContextPath() + "/video?msg=" + (edit ? "updated" : "added"));
    }
}
