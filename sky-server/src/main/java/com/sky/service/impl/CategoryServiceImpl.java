package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description CategoryServiceImpl
 * @Author Zhilin
 * @Date 2023-09-23
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    // 根据名字注入
    @Resource
    private SetmealMapper setmealMapper;

    /**
     * @param categoryDTO
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.addCategory(category);

    }

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult displayCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        List list = categoryMapper.displayCategory(categoryPageQueryDTO.getName(), categoryPageQueryDTO.getType());
        Page pages = (Page) list;
        PageResult pageResult = new PageResult(pages.getTotal(), pages.getResult());
        return pageResult;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void deleteCategory(Integer id) {

        // 查找是否有dish在该分类
        Integer dishCount = dishMapper.countByCategoryId(id);

        if (dishCount != 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        // 查找是否有setmeal在该分类
        Integer setmealCount = setmealMapper.countByCategoryId(id);

        if (setmealCount != 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }


        categoryMapper.deleteCategory(id);

    }

    /**
     * @param categoryDTO
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        // category.setUpdateTime(LocalDateTime.now());
        // category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.updateCategory(category);

    }

    /**
     * @param category
     */
    @Override
    public void changeStatus(Category category) {
        // category.setUpdateUser(BaseContext.getCurrentId());
        // category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateCategory(category);
    }

    /**
     * @param type
     * @return
     */
    @Override
    public List findCategoryByType(Integer type) {

        List category = categoryMapper.findCategoryByType(type);

        return category;
    }

    @Override

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
