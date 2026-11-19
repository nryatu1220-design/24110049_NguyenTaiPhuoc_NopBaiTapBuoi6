package vn.iotstar.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;

/** Chi tiết + phát một video: /watch?id=... (dùng cho cả trang /home và /video). */
@WebServlet(urlPatterns = { "/watch" })
public class WatchController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IVideoService videoService = new VideoServiceImpl();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Video video = videoService.findById(id);

        HttpSession session = req.getSession(false);
        Object uid = session == null ? null : session.getAttribute("userId");
        boolean owner = video != null && video.getUser() != null && uid != null
                && video.getUser().getUserId() == (Integer) uid;

        // Video đang ẩn (active = false) chỉ chủ sở hữu mới xem được
        if (video == null || (!video.isActive() && !owner)) {
            req.setAttribute("error", "Video không tồn tại hoặc đã bị ẩn.");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.getRequestDispatcher("/views/web/watch.jsp").forward(req, resp);
            return;
        }

        // Mỗi phiên chỉ tính 1 lượt xem cho mỗi video (tránh tăng khi F5)
        HttpSession s = req.getSession(true);
        Set<String> viewed = (Set<String>) s.getAttribute("viewedVideos");
        if (viewed == null) {
            viewed = new HashSet<>();
            s.setAttribute("viewedVideos", viewed);
        }
        if (viewed.add(video.getVideoId())) {
            videoService.increaseViews(video.getVideoId());
            video.setViews(video.getViews() + 1);
        }

        req.setAttribute("video", video);
        req.setAttribute("owner", owner);
        req.getRequestDispatcher("/views/web/watch.jsp").forward(req, resp);
    }
}
