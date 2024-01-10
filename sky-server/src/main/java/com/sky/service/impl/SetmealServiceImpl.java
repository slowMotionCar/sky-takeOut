package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description SetmealServiceImpl
 * @Author Zhilin
 * @Date 2023-09-25
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * @param setmealDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSetmeal(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 添加setmeal表
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.addSetmeal(setmeal);
        // 添加setmealdish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 获取id
        Long setmealId = setmeal.getId();
        setmealDishes.forEach((setmealDish) ->
        {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.addSetmealDish(setmealDishes);


    }

    /**
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult setmealQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 获取查找字段
        Integer categoryId = setmealPageQueryDTO.getCategoryId();
        Integer status = setmealPageQueryDTO.getStatus();
        String name = setmealPageQueryDTO.getName();

        List<SetmealVO> list = setmealMapper.setmealQuery(categoryId, status, name);

        Page pages = (Page) list;

        return PageResult.builder()
                .total(pages.getTotal())
                .records(pages.getResult())
                .build();
    }

    /**
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSetmeal(List<Long> ids) {

        // 在售状态下不可删除套餐
        // 判断几个在卖
        Integer isOnSale = setmealMapper.countOnSale(ids);
        if (isOnSale != 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        // 删除setmeal表
        setmealMapper.deletSetmeal(ids);
        // 删除setmeal dish表
        setmealDishMapper.deleteSetmealDish(ids);

    }

    /**
     * @param id
     * @return
     */
    @Override
    public SetmealVO findById(long id) {

        // 拷贝元数据
        Setmeal setmeal = setmealMapper.findById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        // category名字
        Long categoryId = setmeal.getCategoryId();
        String categoryById = categoryMapper.findCategoryById(categoryId);
        setmealVO.setCategoryName(categoryById);
        log.info("categoryname:{}", categoryById);

        // 查setmealdish
        // List<SetmealDish> list = new ArrayList<>();
        List<SetmealDish> list = setmealDishMapper.findSetmealBySetmealId(id);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * @param setmealDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSetmeal(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 更新Setmeal
        setmealMapper.updateSetmeal(setmeal);
        // 删除SetmealDish
        Long id = setmealDTO.getId();
        List list = new ArrayList<>();
        list.add(id);
        setmealDishMapper.deleteSetmealDish(list);
        // 重新添加Setmealdish

        List<SetmealDish>  setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishList.forEach((item)->{
            item.setSetmealId(id);
        });

        setmealDishMapper.addSetmealDish(setmealDishList);
    }

    /**
     *
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, long id) {

        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);

        setmealMapper.updateSetmeal(setmeal);


    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
