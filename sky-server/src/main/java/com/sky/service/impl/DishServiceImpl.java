package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description DishServiceImpl
 * @Author Zhilin
 * @Date 2023-09-24
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param dishDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDish(DishDTO dishDTO) {
        // 菜品不能没有分类
        if (dishDTO.getCategoryId() == null) {
            throw new BaseException("菜品不能没有分类");
        }
        // 菜品不能没有分类
        if (dishDTO.getFlavors().size() == 0) {
            throw new BaseException("菜品不能没有口味");
        }

        // 开始添加菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(0);
        dishMapper.addDish(dish);

        // 开始添加口味
        DishFlavor dishFlavor = new DishFlavor();

        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        flavors.forEach((flavor) -> {
            flavor.setDishId(dishId);
            // dishFlavorMapper.addDishFlavor(flavor);
        });

        System.out.println(flavors);
        dishFlavorMapper.addDishFlavorAll(flavors);

        clearCache("dish_*");
    }

    /**
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult showDish(DishPageQueryDTO dishPageQueryDTO) {
        // pagehelper
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        // 这里用了一条SQL语句 连表查询
        List<DishVO> list = dishMapper.showDish(dishPageQueryDTO.getName(), dishPageQueryDTO.getCategoryId(), dishPageQueryDTO.getStatus());
        Page pages = (Page) list;
        PageResult pageResult = new PageResult(pages.getTotal(), pages.getResult());
        return pageResult;
    }

    /**删除菜品
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDish(List ids) {
        // 其实这里遍历不太好, 会影响效率 多次读写数据库
        ids.forEach((id) -> {

            // 起售中的菜品不能删除
            Integer isSaling = dishMapper.isSaling((Integer) id);
            log.info("菜品状态{}", isSaling);
            if (isSaling != null && isSaling == 1) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            // 被套餐关联的菜品不能删除
            Integer isConnectWithSetmeal = setmealDishMapper.isConnectWithSetmeal(id);
            log.info("是否关联套餐{}", isConnectWithSetmeal);
            if (isConnectWithSetmeal != 0) {
                throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
            }
            // 情况都排除了,执行删除
            // 先删除菜品
            dishMapper.deleteDish(ids);
            // 删除菜品口味列表数据
            dishFlavorMapper.deleteDishFlavor(ids);
        });

        clearCache("dish_*");
    }
        private void clearCache(String keyPattern){
            Set keys = redisTemplate.keys(keyPattern);
            redisTemplate.delete(keys);
        }
    /**
     * @param id
     * @return
     */
    @Override
    public DishVO findDishById(long id) {

        DishVO dishReturn = new DishVO();
        Dish dish = dishMapper.findDishById(id);

        // 封装
        BeanUtils.copyProperties(dish, dishReturn);

        // 改名
        Long categoryId = dishReturn.getCategoryId();
        String name = categoryMapper.findCategoryById(categoryId);
        dishReturn.setCategoryName(name);

        // 注入flavor

        List dishFlavor = dishFlavorMapper.findDishFlavorByDishId(id);
        dishReturn.setFlavors(dishFlavor);

        return dishReturn;
    }

    /**
     * @param dishDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品
        dishMapper.updateDish(dish);
        // 删除口味根据id
        List list = new ArrayList<>();
        list.add(dish.getId());
        dishFlavorMapper.deleteDishFlavor(list);
        // 增加口味根据id
        List<DishFlavor> dishFlavorNew = dishDTO.getFlavors();
        Long id = dishDTO.getId();

        dishFlavorNew.forEach((dishFlavor) -> {
            dishFlavor.setDishId(id);
            dishFlavorMapper.addDishFlavor(dishFlavor);
        });
        clearCache("dish_*");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDishStatus(Integer status, Long id) {
        Dish dish = Dish.builder().id(id).status(status).build();
        // 该菜品停售-更改状态
        dishMapper.updateDish(dish);
        // 包含该菜品的套餐停售
        Long setmealID = setmealDishMapper.findSetmealByDishId(id);
        Setmeal setmeal = new Setmeal();
        setmeal.setId(setmealID);
        setmeal.setStatus(status);
        setmealMapper.updateSetmeal(setmeal);
    }

    @Override
    public List<Dish> listByCategoryId(Long categoryId) {

        List<Dish> dishes = dishMapper.listByCategoryId(categoryId);
        List<DishVO> dishVOS = new ArrayList<>();
        DishVO tempDishVo = new DishVO();
        dishes.forEach((dish)->{

            BeanUtils.copyProperties(dish, tempDishVo);
            dishVOS.add(tempDishVo);

        });

        return dishes;
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.findDishFlavorByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
