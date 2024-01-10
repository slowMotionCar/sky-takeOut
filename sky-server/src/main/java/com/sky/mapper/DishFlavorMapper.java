package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description DishFlavor
 * @Author Zhilin
 * @Date 2023-09-24
 */
@Mapper
public interface DishFlavorMapper {
    void addDishFlavor(DishFlavor dishFlavor);

    void addDishFlavorAll(List<DishFlavor> flavors);

    void deleteDishFlavor(List ids);

    List<DishFlavor> findDishFlavorByDishId(long id);

    Integer countDishFlavorByDishId(long id);
}
