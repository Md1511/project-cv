package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    public Category saveCategory(Category category);
    public Boolean existCategory(String categoryName);
    public List<Category> getAllCategories();
    public Boolean deleteCateogry(int id);
    public Category getCategoryById(int id);
    public List<Category> getAllActiveCategory();
    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);
}
