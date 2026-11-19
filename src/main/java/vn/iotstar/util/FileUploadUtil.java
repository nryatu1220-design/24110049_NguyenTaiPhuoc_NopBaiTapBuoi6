package vn.iotstar.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.Part;

/** Xử lý lưu file upload, dùng chung cho Add và Edit. */
public class FileUploadUtil {

    private FileUploadUtil() {
    }

    /**
     * Lưu file upload vào Constant.DIR với tên mới theo thời gian.
     *
     * @return tên file đã lưu, hoặc null nếu người dùng không chọn file
     */
    public static String saveFile(Part part) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isEmpty()) {
            return null;
        }

        String original = Paths.get(submitted).getFileName().toString();
        int index = original.lastIndexOf('.');
        String ext = index >= 0 ? original.substring(index + 1).toLowerCase() : "";

        File uploadDir = new File(Constant.DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String newName = System.currentTimeMillis() + (ext.isEmpty() ? "" : "." + ext);
        part.write(new File(uploadDir, newName).getAbsolutePath());
        return newName;
    }

    /**
     * Lưu file upload vào thư mục dir với tên ngẫu nhiên (UUID) giữ nguyên phần mở rộng.
     *
     * @param allowedExt các phần mở rộng cho phép (chữ thường, không có dấu chấm)
     * @return tên file đã lưu, hoặc null nếu người dùng không chọn file
     * @throws IllegalArgumentException nếu phần mở rộng không được phép
     */
    public static String saveFile(Part part, String dir, Set<String> allowedExt) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isEmpty()) {
            return null;
        }
        String original = Paths.get(submitted).getFileName().toString();
        int index = original.lastIndexOf('.');
        String ext = index >= 0 ? original.substring(index + 1).toLowerCase() : "";
        if (!allowedExt.contains(ext)) {
            throw new IllegalArgumentException("Định dạng file không được hỗ trợ (cho phép: "
                    + String.join(", ", allowedExt) + ")");
        }

        File uploadDir = new File(dir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String newName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        part.write(new File(uploadDir, newName).getAbsolutePath());
        return newName;
    }

    /** Xoá file trong dir, bỏ qua nếu không tồn tại. Chỉ dùng phần tên file để tránh ../ */
    public static void deleteQuietly(String dir, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            File f = new File(dir, Paths.get(fileName).getFileName().toString());
            if (f.isFile()) {
                f.delete();
            }
        } catch (Exception ignored) {
            // không để lỗi xoá file làm hỏng thao tác chính
        }
    }
}
