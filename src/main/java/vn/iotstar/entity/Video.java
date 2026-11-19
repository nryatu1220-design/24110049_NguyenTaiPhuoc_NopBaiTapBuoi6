package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v ORDER BY v.createdDate DESC")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "videoId")
    private String videoId;

    @Column(name = "active")
    private boolean active;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX) NULL")
    private String description;

    @Column(name = "poster", columnDefinition = "NVARCHAR(255) NULL")
    private String poster;

    @Column(name = "title", columnDefinition = "NVARCHAR(255) NULL")
    private String title;

    @Column(name = "views")
    private int views;

    /** Tên file video đã upload (lưu trong Constant.VIDEO_DIR). */
    @Column(name = "videoFile", columnDefinition = "NVARCHAR(255) NULL")
    private String videoFile;

    @Column(name = "createdDate")
    private LocalDateTime createdDate = LocalDateTime.now();

    // many-to-one: nhiều Video thuộc về một User (quan hệ 1 - n giữa users và videos)
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    // bi-directional many-to-one association to Category
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    public Video() {
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /** Dùng cho JSP (fmt không format được LocalDateTime). Không phải cột trong DB. */
    public String getCreatedDateText() {
        return createdDate == null ? "" : createdDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
