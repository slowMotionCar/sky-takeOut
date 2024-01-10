package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description DishMapper
 * @Author Zhilin
 * @Date 2023-09-24
 */
@Mapper
public interface DishMapper {


    Integer countByCategoryId(Integer id);

    @Autofill(value = OperationType.INSERT)
    void addDish(Dish dish);

    List<DishVO> showDish(String name, Integer categoryId, Integer status);

    Integer isSaling(Integer id);

    void deleteDish(List ids);

    Dish findDishById(long id);

    @Autofill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    List<Dish> listByCategoryId(Long categoryId);


    /**
     * 动态条件查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    Integer countByStatus(Integer status);
}
