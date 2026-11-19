package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;

/** Trang chủ: hiển thị 10 video mới nhất. */
@WebServlet(urlPatterns = { "/home" })
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int LATEST = 10;

    private final IVideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("videos", videoService.findLatestActive(LATEST));
        req.getRequestDispatcher("/views/web/home.jsp").forward(req, resp);
    }
}
