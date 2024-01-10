package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description CategoryMapper
 * @Author Zhilin
 * @Date 2023-09-23
 */
@Mapper
public interface CategoryMapper {
    @Autofill(value = OperationType.INSERT)
    void addCategory(Category category);

    List displayCategory(String name, Integer type);

    void deleteCategory(Integer id);

    @Autofill(value = OperationType.UPDATE)
    void updateCategory(Category category);

    List findCategoryByType(Integer type);

    String findCategoryById(Long categoryId);

    List<Category> list(Integer type);
}
