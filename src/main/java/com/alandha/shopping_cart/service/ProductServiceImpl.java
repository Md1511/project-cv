package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.Product;
import com.alandha.shopping_cart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repo;

    @Override
    public Product saveProduct(Product product) {
       return repo.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = repo.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product)) {
            repo.deleteById(id);
            return true;
        }
        return false;
        }

    @Override
    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;

        if(ObjectUtils.isEmpty(category)) {
            products =  repo.findByIsActiveTrue();
        } else {
            products = repo.findByCategoryAndIsActiveTrue(category);

        }

        return products;
    }

    @Override
    public List<Product> searchProduct(String keyword) {

        List<Product> products = repo.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword,keyword);
        return products;

    }

    @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> products = null;

        if(ObjectUtils.isEmpty(category)) {
            products =  repo.findByIsActiveTrue(pageable);
        } else {
            products = repo.findByCategory(pageable, category);
        }

        return products;
    }

    @Override
    public Product findByTitle(String title) {
        return repo.findByTitle(title);
    }

    @Override
    public Page<Product> getAllProductPagination2(Integer pageNo, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repo.findAll(pageable);
    }



    @Override
    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String ch) {


        Page<Product> products = null;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        products = repo.findByIsActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);

        return products;
    }
}

