package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * @Description CategoryService
 * @Author Zhilin
 * @Date 2023-09-23
 */
public interface CategoryService {
    /**
     *
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     *
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult displayCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     *
     * @param id
     */
    void deleteCategory(Integer id);

    /**
     *
     * @param categoryDTO
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     *
     * @param category
     */
    void changeStatus(Category category);

    List<Category> findCategoryByType(Integer type);

    List<Category> list(Integer type);
}
