package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description SetmealMapper
 * @Author Zhilin
 * @Date 2023-09-24
 */
@Mapper
public interface SetmealMapper {
    Integer countByCategoryId(Integer id);

    @Autofill(OperationType.UPDATE)

    void updateSetmeal(Setmeal setmeal);


    @Autofill(value = OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    List<SetmealVO> setmealQuery(Integer categoryId, Integer status, String name);

    Integer countOnSale(List<Long> ids);

    void deletSetmeal(List<Long> ids);

    Setmeal findById(long id);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    Integer countByStatus(Integer integer);
}
