package vn.iotstar.dao;

import vn.iotstar.entity.User;

public interface IUserDao {

    void insert(User user);

    /** Cập nhật user (mật khẩu, trạng thái kích hoạt, OTP...). */
    void update(User user);

    void delete(int userId);

    /** Trả về null nếu không có. */
    User findById(int userId);

    /** Trả về null nếu không có. */
    User findByUsername(String username);

    /** Trả về null nếu không có. */
    User findByEmail(String email);
}
