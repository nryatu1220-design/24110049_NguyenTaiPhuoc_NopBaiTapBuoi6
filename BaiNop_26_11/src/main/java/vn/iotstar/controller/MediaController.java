package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

/**
 * Trả file video / poster đã upload: /media?fname=abc.mp4
 * Hỗ trợ HTTP Range để thẻ &lt;video&gt; tua (seek) được.
 */
@WebServlet(urlPatterns = { "/media" })
public class MediaController extends HttpServlet {
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
        File file = new File(Constant.VIDEO_DIR, fileName);
        if (!file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long length = file.length();
        long start = 0;
        long end = length - 1;
        boolean partial = false;

        String range = req.getHeader("Range");
        if (range != null && range.startsWith("bytes=")) {
            try {
                String spec = range.substring(6).split(",")[0].trim();
                int dash = spec.indexOf('-');
                String from = spec.substring(0, dash);
                String to = spec.substring(dash + 1);
                if (from.isEmpty()) { // bytes=-500 : 500 byte cuối
                    start = Math.max(0, length - Long.parseLong(to));
                } else {
                    start = Long.parseLong(from);
                    if (!to.isEmpty()) {
                        end = Math.min(end, Long.parseLong(to));
                    }
                }
                if (start > end || start >= length) {
                    resp.setHeader("Content-Range", "bytes */" + length);
                    resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return;
                }
                partial = true;
            } catch (RuntimeException e) {
                start = 0;
                end = length - 1; // Range lỗi -> trả toàn bộ file
            }
        }

        String mime = getServletContext().getMimeType(file.getName());
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("Cache-Control", "public, max-age=86400"); // tên file là UUID nên không đổi
        if (partial) {
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
        }
        resp.setContentLengthLong(end - start + 1);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);
            OutputStream out = resp.getOutputStream();
            byte[] buf = new byte[16 * 1024];
            long remaining = end - start + 1;
            while (remaining > 0) {
                int n = raf.read(buf, 0, (int) Math.min(buf.length, remaining));
                if (n < 0) {
                    break;
                }
                out.write(buf, 0, n);
                remaining -= n;
            }
        } catch (IOException e) {
            // Trình duyệt ngắt kết nối giữa chừng (tua video) là bình thường
        }
    }
}
