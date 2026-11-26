# ShoppingJPA – Spring Boot 4, JSP/JSTL và JPA

Ứng dụng chạy trên **Spring Boot 4** dưới dạng executable WAR, giữ JSP/JSTL và các Servlet hiện có. Tầng dữ liệu dùng JPA (Hibernate) với SQL Server.

## Yêu cầu
- JDK 21+, Maven
- Tomcat 11+ nếu triển khai WAR lên Tomcat ngoài (dùng `jakarta.*`)
- SQL Server (TCP/IP port 1433, bật SQL Authentication cho user `sa`)

## Cách chạy
1. Chạy `sql/create-database.sql` bằng SQL Server Management Studio để tạo database `ShoppingJPA`.
2. Kiểm tra địa chỉ server và `databaseName` trong `src/main/resources/META-INF/persistence.xml`. Cấu hình hiện tại dùng Windows Integrated Authentication; tài khoản Windows chạy ứng dụng phải có quyền truy cập SQL Server. Nếu dùng SQL Authentication, cấu hình URL và thông tin đăng nhập JDBC tương ứng, không commit mật khẩu vào Git.
3. (Tuỳ chọn) Chạy `vn.iotstar.config.Test` (main) để Hibernate tạo bảng và thêm dữ liệu thử.
4. Chạy `mvn spring-boot:run` hoặc `mvn clean package` rồi `java -jar target/ShoppingJPA.war`.
5. Khi chạy bằng embedded Tomcat, mở `http://localhost:8080/`. Nếu deploy WAR lên Tomcat 11+, mở `http://localhost:8080/ShoppingJPA/`.

Ảnh upload lưu ở `<thư mục home>/upload/category`, poster + video lưu ở `<thư mục home>/upload/video` (xem `util/Constant.java`).

### Cấu hình email OTP
Dùng Gmail thì bật xác minh 2 bước và tạo *App password* (không dùng mật khẩu Gmail thường). Để giữ thông tin ngoài project/Git, tạo file `%USERPROFILE%\.shopping-mail.properties`:

```properties
mail.username=dia-chi-gmail@gmail.com
mail.password=mat-khau-ung-dung-16-ky-tu-khong-co-khoang-trang
mail.from=dia-chi-gmail@gmail.com
mail.from.name=Shopping
```

Sau đó khởi động lại Tomcat. Có thể tham khảo `src/main/resources/mail.properties` để biết các khóa SMTP. Nếu chưa cấu hình, mã OTP được **in ra console của Tomcat** (chế độ DEV) để chạy thử.

## Cấu trúc
```
sql/create-database.sql
src/main/resources/META-INF/persistence.xml     persistence unit "jpa-shopping"
src/main/java/vn/iotstar/
  entity/     Category.java, Video.java, User.java   (@Entity)
  config/     JPAConfig.java (EntityManagerFactory dùng chung), JPAContextListener.java, Test.java
  dao/        ICategoryDao, IUserDao + impl/
  service/    ICategoryService, IUserService + impl/
  controller/ Login, Register, Logout, DownloadImage, admin/{Dashboard,CategoryList,CategoryAdd,CategoryEdit,CategoryDelete}
  filter/     AuthenticationFilter.java (bảo vệ /admin/*)
src/main/webapp/views/                           JSP + JSTL
src/main/webapp/WEB-INF/sitemesh3.xml            SiteMesh 3 decorator mapping
src/main/webapp/WEB-INF/decorators/bootstrap.html Bootstrap 5 shared template
```

## Giao diện và validation
- SiteMesh 3 áp dụng một Bootstrap 5 template cho các trang HTML; Bootstrap CSS/JS được tải từ jsDelivr.
- Các form đăng nhập, đăng ký, quên mật khẩu, thêm/sửa danh mục và thêm/sửa video đều kiểm tra dữ liệu ở server; các form cũng khai báo ràng buộc phù hợp trên trình duyệt.
- Ảnh danh mục chỉ nhận JPG, JPEG, PNG hoặc WEBP; dung lượng upload tối đa 5 MB.

## URL
| URL | Chức năng |
|---|---|
| `/register` | Đăng ký tài khoản (lưu bảng `users`, mật khẩu băm PBKDF2) |
| `/login`, `/logout` | Đăng nhập / đăng xuất |
| `/admin/dashboard` | Tổng quan |
| `/admin/category/list[?page=2]` hoặc `?keyword=...` | Danh sách (phân trang) / tìm kiếm |
| `/admin/category/add` | Thêm (GET form, POST lưu) |
| `/admin/category/edit?id=1` | Sửa (GET form, POST lưu) |
| `/admin/category/delete?id=1` | Xóa |
| `/admin/user/list[?page=2&keyword=...]` | Danh sách người dùng, tìm kiếm theo tên đăng nhập/họ tên/email và phân trang |
| `/admin/user/add`, `/admin/user/edit?id=1`, `POST /admin/user/delete` | CRUD người dùng |

| `/register` → `/verify-otp` | Đăng ký rồi nhập OTP gửi qua email để **kích hoạt** tài khoản (OTP 6 số, hiệu lực 5 phút, sai tối đa 5 lần) |
| `/forgot-password` → `/reset-password` | Quên mật khẩu: nhập email → nhận OTP → đặt mật khẩu mới |
| `/home` | Trang chủ: 10 video mới nhất; bấm tiêu đề để xem |
| `/watch?id=...` | Chi tiết + phát video (HTML5 `<video>`) |
| `/video[?page=2]` | Video của user đăng nhập, **6 video / trang** |
| `/video/add`, `/video/edit?id=...`, `POST /video/delete` | CRUD Video (upload poster + file video bằng `@MultipartConfig`) |
| `/media?fname=...` | Trả file poster / video (hỗ trợ HTTP Range để tua video) |

Quan hệ: `users` 1 — n `videos` (cột `videos.userId`). Mật khẩu băm PBKDF2-HMAC-SHA256 kèm salt ngẫu nhiên.
