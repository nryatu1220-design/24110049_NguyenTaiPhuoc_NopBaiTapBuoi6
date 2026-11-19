package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Category;

public interface ICategoryDao {

    void insert(Category category);

    void update(Category category);

    void delete(int cateid) throws Exception;

    Category findById(int cateid);

    /** Trả về null nếu không có danh mục trùng tên. */
    Category findByCategoryname(String name);

    List<Category> findAll();

    /** Phân trang: page bắt đầu từ 0. */
    List<Category> findAll(int page, int pagesize);

    List<Category> searchByName(String catname);

    int count();
}
