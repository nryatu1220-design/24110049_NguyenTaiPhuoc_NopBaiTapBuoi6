package vn.iotstar.service;

import java.util.List;

import vn.iotstar.entity.Category;

public interface ICategoryService {

    /** @throws IllegalArgumentException nếu tên danh mục đã tồn tại */
    void insert(Category category);

    /** @throws IllegalArgumentException nếu tên mới trùng với danh mục khác */
    void update(Category category);

    void delete(int cateid);

    Category findById(int cateid);

    Category findByCategoryname(String name);

    List<Category> findAll();

    List<Category> findAll(int page, int pagesize);

    List<Category> searchByName(String catname);

    int count();
}
