package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private int userId;

    @Column(name = "username", columnDefinition = "NVARCHAR(50)", nullable = false, unique = true)
    private String username;

    /** Mật khẩu đã băm (PBKDF2), không bao giờ lưu mật khẩu gốc. */
    @Column(name = "password", columnDefinition = "NVARCHAR(255)", nullable = false)
    private String password;

    @Column(name = "fullname", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String fullname;

    @Column(name = "email", columnDefinition = "NVARCHAR(100)", nullable = false, unique = true)
    private String email;

    @Column(name = "createdDate")
    private LocalDateTime createdDate = LocalDateTime.now();

    /** false = chưa kích hoạt bằng OTP. DEFAULT 1 để tài khoản cũ trong DB vẫn đăng nhập được. */
    @Column(name = "active", columnDefinition = "BIT NOT NULL DEFAULT 1")
    private boolean active;

    /** Mã OTP đang chờ xác nhận (kích hoạt tài khoản hoặc quên mật khẩu). */
    @Column(name = "otp", columnDefinition = "NVARCHAR(10) NULL")
    private String otp;

    /** ACTIVATE hoặc RESET. */
    @Column(name = "otpPurpose", columnDefinition = "NVARCHAR(20) NULL")
    private String otpPurpose;

    @Column(name = "otpExpiry")
    private LocalDateTime otpExpiry;

    /** Số lần nhập sai OTP hiện tại. */
    @Column(name = "otpAttempts", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int otpAttempts;

    // one-to-many: một User có nhiều Video
    @OneToMany(mappedBy = "user")
    private List<Video> videos = new ArrayList<>();

    public User() {
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getOtpPurpose() { return otpPurpose; }
    public void setOtpPurpose(String otpPurpose) { this.otpPurpose = otpPurpose; }
    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }
    public int getOtpAttempts() { return otpAttempts; }
    public void setOtpAttempts(int otpAttempts) { this.otpAttempts = otpAttempts; }
    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos; }
}
