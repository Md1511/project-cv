package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.Category;
import com.alandha.shopping_cart.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository repo;


    @Override
    public Category saveCategory(Category category) {
        return repo.save(category);
    }

    @Override
    public Boolean existCategory(String categoryName) {
        return repo.existsByName(categoryName);
    }

    @Override
    public List<Category> getAllCategories() {
        return repo.findAll();
    }

    @Override
    public Boolean deleteCateogry(int id) {

        Category category = repo.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(category)) {
            repo.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(int id) {
       return repo.findById(id).orElse(null);
//      return category;
    }

    @Override
    public List<Category> getAllActiveCategory() {

        List<Category> categories = repo.findByIsActiveTrue();
        return categories;
    }

    @Override
    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repo.findAll(pageable);
    }


}
