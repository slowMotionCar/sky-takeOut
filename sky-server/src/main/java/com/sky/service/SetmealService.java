package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @Description SetmealService
 * @Author Zhilin
 * @Date 2023-09-25
 */

/**
 *
 */
public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    /**
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult setmealQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmeal(List<Long> ids);

    SetmealVO findById(long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    void changeStatus(Integer status, long id);


    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

}
