package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.Part;

/** Upload ảnh hồ sơ multipart lên Cloudinary. */
public final class CloudinaryUtil {

    private static final Properties CONFIG = load();
    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;

    private CloudinaryUtil() {}

    public static String uploadProfileImage(Part part) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        if (part.getSize() > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("Ảnh tối đa 5 MB");
        }
        String contentType = part.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ được tải lên file ảnh");
        }
        String cloudName = value("cloudinary.cloud_name", "CLOUDINARY_CLOUD_NAME");
        if (cloudName.isBlank() || cloudName.startsWith("your_")) {
            throw new IllegalStateException("Chưa cấu hình Cloudinary. Hãy thiết lập biến môi trường hoặc cloudinary.properties");
        }

        byte[] fileBytes;
        try (InputStream in = part.getInputStream()) {
            fileBytes = in.readAllBytes();
        }
        String boundary = "shopping-profile-" + Instant.now().toEpochMilli();
        String uploadPreset = value("cloudinary.upload_preset", "CLOUDINARY_UPLOAD_PRESET");
        String apiKey = value("cloudinary.api_key", "CLOUDINARY_API_KEY");
        String apiSecret = value("cloudinary.api_secret", "CLOUDINARY_API_SECRET");

        StringBuilder body = new StringBuilder();
        appendField(body, boundary, "folder", "shopping/profile");
        if (uploadPreset != null && !uploadPreset.isBlank() && !uploadPreset.startsWith("your_")) {
            appendField(body, boundary, "upload_preset", uploadPreset);
        } else {
            if (apiKey.isBlank() || apiSecret.isBlank()) {
                throw new IllegalStateException("Thiếu CLOUDINARY_API_KEY hoặc CLOUDINARY_API_SECRET");
            }
            String timestamp = Long.toString(Instant.now().getEpochSecond());
            appendField(body, boundary, "timestamp", timestamp);
            appendField(body, boundary, "api_key", apiKey);
            appendField(body, boundary, "signature", sign("folder=shopping/profile&timestamp=" + timestamp, apiSecret));
        }

        String submittedName = part.getSubmittedFileName();
        String fileName = submittedName == null || submittedName.isBlank() ? "avatar.jpg" : Path.of(submittedName).getFileName().toString();
        body.append("--").append(boundary).append("\r\n");
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(fileName.replace("\"", "")).append("\"\r\n");
        body.append("Content-Type: ").append(contentType).append("\r\n\r\n");
        byte[] prefix = body.toString().getBytes(StandardCharsets.UTF_8);
        byte[] suffix = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] multipart = new byte[prefix.length + fileBytes.length + suffix.length];
        System.arraycopy(prefix, 0, multipart, 0, prefix.length);
        System.arraycopy(fileBytes, 0, multipart, prefix.length, fileBytes.length);
        System.arraycopy(suffix, 0, multipart, prefix.length + fileBytes.length, suffix.length);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipart))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Cloudinary từ chối upload ảnh: " + response.statusCode());
            }
            Matcher matcher = Pattern.compile("\"secure_url\"\\s*:\\s*\"([^\"]+)\"")
                    .matcher(response.body());
            if (!matcher.find()) {
                throw new IllegalStateException("Cloudinary không trả về secure_url");
            }
            return matcher.group(1).replace("\\/", "/");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Upload ảnh bị gián đoạn", e);
        }
    }

    private static void appendField(StringBuilder body, String boundary, String name, String value) {
        body.append("--").append(boundary).append("\r\n");
        body.append("Content-Disposition: form-data; name=\"").append(name).append("\"\r\n\r\n");
        body.append(value).append("\r\n");
    }

    private static String sign(String data, String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest((data + secret).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Không tạo được chữ ký Cloudinary", e);
        }
    }

    private static String value(String property, String environment) {
        String env = System.getenv(environment);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return CONFIG.getProperty(property, "").trim();
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = CloudinaryUtil.class.getClassLoader().getResourceAsStream("cloudinary.properties")) {
            if (in != null) {
                p.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được cloudinary.properties", e);
        }
        Path local = Path.of(System.getProperty("user.home"), ".shopping-cloudinary.properties");
        if (Files.isRegularFile(local)) {
            try (InputStream in = Files.newInputStream(local)) {
                p.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new IllegalStateException("Không đọc được cấu hình Cloudinary local", e);
            }
        }
        return p;
    }
}
