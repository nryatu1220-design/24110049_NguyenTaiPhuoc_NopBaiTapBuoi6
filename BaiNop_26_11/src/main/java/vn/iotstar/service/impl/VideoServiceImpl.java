package vn.iotstar.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.dao.impl.UserDao;
import vn.iotstar.dao.impl.VideoDao;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FileUploadUtil;

public class VideoServiceImpl implements IVideoService {

    private final IVideoDao videoDao = new VideoDao();
    private final IUserDao userDao = new UserDao();

    private void validate(Video video) {
        String title = video.getTitle() == null ? "" : video.getTitle().trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề video không được để trống");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Tiêu đề tối đa 255 ký tự");
        }
        video.setTitle(title);
    }

    @Override
    public void insert(Video video, int userId) {
        validate(video);
        if (video.getVideoFile() == null || video.getVideoFile().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn file video để upload");
        }
        User owner = userDao.findById(userId);
        if (owner == null) {
            throw new IllegalArgumentException("Tài khoản không tồn tại");
        }
        video.setVideoId(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        video.setUser(owner);
        video.setCreatedDate(LocalDateTime.now());
        video.setViews(0);
        videoDao.insert(video);
    }

    @Override
    public void update(Video video, int userId) {
        Video old = findOwned(video.getVideoId(), userId);
        if (old == null) {
            throw new IllegalArgumentException("Không tìm thấy video hoặc bạn không có quyền sửa");
        }
        validate(video);
        videoDao.update(video);
    }

    @Override
    public void delete(String videoId, int userId) {
        Video video = findOwned(videoId, userId);
        if (video == null) {
            throw new IllegalArgumentException("Không tìm thấy video hoặc bạn không có quyền xoá");
        }
        videoDao.delete(videoId);
        FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, video.getVideoFile());
        FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, video.getPoster());
    }

    @Override
    public Video findById(String videoId) {
        return videoId == null || videoId.isEmpty() ? null : videoDao.findById(videoId);
    }

    @Override
    public Video findOwned(String videoId, int userId) {
        Video v = findById(videoId);
        return v != null && v.getUser() != null && v.getUser().getUserId() == userId ? v : null;
    }

    @Override
    public List<Video> findLatestActive(int limit) {
        return videoDao.findLatestActive(limit);
    }

    @Override
    public List<Video> findByUser(int userId, int page, int pagesize) {
        return videoDao.findByUser(userId, Math.max(page, 1) - 1, pagesize);
    }

    @Override
    public long countByUser(int userId) {
        return videoDao.countByUser(userId);
    }

    @Override
    public void increaseViews(String videoId) {
        videoDao.increaseViews(videoId);
    }
}
