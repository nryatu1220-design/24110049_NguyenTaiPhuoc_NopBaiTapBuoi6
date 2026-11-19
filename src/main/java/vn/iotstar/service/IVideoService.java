package vn.iotstar.service;

import java.util.List;

import vn.iotstar.entity.Video;

public interface IVideoService {

    /** Thêm video cho user (tự sinh videoId, createdDate). */
    void insert(Video video, int userId);

    /** Cập nhật video; chỉ chủ sở hữu mới được sửa. */
    void update(Video video, int userId);

    /** Xoá video (và file đã upload); chỉ chủ sở hữu mới được xoá. */
    void delete(String videoId, int userId);

    Video findById(String videoId);

    /** Trả về video nếu thuộc về user, ngược lại null. */
    Video findOwned(String videoId, int userId);

    /** Trang chủ: 10 video active mới nhất. */
    List<Video> findLatestActive(int limit);

    /** Phân trang video của user. page bắt đầu từ 1. */
    List<Video> findByUser(int userId, int page, int pagesize);

    long countByUser(int userId);

    void increaseViews(String videoId);
}
