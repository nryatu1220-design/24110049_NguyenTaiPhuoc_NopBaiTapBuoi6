package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/image") // /image?fname=abc.png
public class DownloadImageController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fileName = req.getParameter("fname");
        if (fileName == null || fileName.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Chỉ lấy phần tên file để chặn kiểu tấn công ../../
        fileName = Paths.get(fileName).getFileName().toString();
        File file = new File(Constant.DIR, fileName);
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mime = getServletContext().getMimeType(file.getName());
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        resp.setContentLengthLong(file.length());
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
