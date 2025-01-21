package com.ecom.service;

import com.ecom.model.Category;

import java.util.List;

public interface CategoryServiceIn {
    public Category saveCategory(Category category);

    public Boolean existsCategory(String name);

    public List<Category> getAllCategory();

    public Boolean deleteCategory(int id);
}
