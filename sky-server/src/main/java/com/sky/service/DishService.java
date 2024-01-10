package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @Description DishService
 * @Author Zhilin
 * @Date 2023-09-24
 */

public interface DishService {
    /**
     * @param dishDTO
     */
    void addDish(DishDTO dishDTO);

    /**
     * @param dishPageQueryDTO
     * @return
     */
    PageResult showDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * @param ids
     */
    void deleteDish(List ids);

    DishVO findDishById(long id);

    void updateDish(DishDTO dishDTO);

    void updateDishStatus(Integer status, Long id);

    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}

