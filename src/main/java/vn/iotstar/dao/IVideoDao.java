package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Video;

public interface IVideoDao {

    void insert(Video video);

    void update(Video video);

    void delete(String videoId);

    /** Trả về null nếu không có. */
    Video findById(String videoId);

    /** N video đang hiển thị (active) mới nhất của mọi user - dùng cho trang chủ. */
    List<Video> findLatestActive(int limit);

    /** Video của một user, mới nhất trước. page bắt đầu từ 0. */
    List<Video> findByUser(int userId, int page, int pagesize);

    long countByUser(int userId);

    void increaseViews(String videoId);
}
