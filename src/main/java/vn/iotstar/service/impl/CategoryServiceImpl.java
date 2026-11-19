package vn.iotstar.service.impl;

import java.io.File;
import java.util.List;

import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.util.Constant;

public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryDao cateDao = new CategoryDao();

    @Override
    public void insert(Category category) {
        if (cateDao.findByCategoryname(category.getCategoryname()) != null) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        cateDao.insert(category);
    }

    @Override
    public void update(Category newCategory) {
        Category old = cateDao.findById(newCategory.getCategoryId());
        if (old == null) {
            return;
        }
        Category sameName = cateDao.findByCategoryname(newCategory.getCategoryname());
        if (sameName != null && sameName.getCategoryId() != old.getCategoryId()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        old.setCategoryname(newCategory.getCategoryname());
        old.setStatus(newCategory.getStatus());

        // Có ảnh mới -> xóa ảnh cũ rồi gán ảnh mới; không có -> giữ ảnh cũ
        if (newCategory.getImages() != null && !newCategory.getImages().isEmpty()) {
            deleteImageFile(old.getImages());
            old.setImages(newCategory.getImages());
        }
        cateDao.update(old);
    }

    @Override
    public void delete(int cateid) {
        try {
            Category category = cateDao.findById(cateid);
            String image = category == null ? null : category.getImages();
            cateDao.delete(cateid);
            deleteImageFile(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Category findById(int cateid) {
        return cateDao.findById(cateid);
    }

    @Override
    public Category findByCategoryname(String name) {
        return cateDao.findByCategoryname(name);
    }

    @Override
    public List<Category> findAll() {
        return cateDao.findAll();
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        return cateDao.findAll(page, pagesize);
    }

    @Override
    public List<Category> searchByName(String catname) {
        return cateDao.searchByName(catname);
    }

    @Override
    public int count() {
        return cateDao.count();
    }

    private void deleteImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        File file = new File(Constant.DIR, fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
