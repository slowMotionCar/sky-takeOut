package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description SetmealDishMapper
 * @Author Zhilin
 * @Date 2023-09-25
 */
@Mapper
public interface SetmealDishMapper {


    Integer isConnectWithSetmeal(Object id);

    Long findSetmealByDishId(Long id);

    void addSetmealDish(List<SetmealDish> setmealDishes);

    void deleteSetmealDish(List<Long> ids);

    List<SetmealDish> findSetmealBySetmealId(long id);
}
