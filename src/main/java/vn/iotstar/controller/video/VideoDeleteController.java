package vn.iotstar.controller.video;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;

/** POST /video/delete  (id=...) : xoá video của chính mình. */
@WebServlet(urlPatterns = { "/video/delete" })
public class VideoDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IVideoService videoService = new VideoServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");
        String msg;
        try {
            videoService.delete(req.getParameter("id"), userId);
            msg = "deleted";
        } catch (IllegalArgumentException e) {
            msg = "notfound";
        }
        resp.sendRedirect(req.getContextPath() + "/video?msg=" + msg);
    }
}
