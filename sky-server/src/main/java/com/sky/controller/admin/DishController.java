package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description DishController
 * @Author Zhilin
 * @Date 2023-09-24
 */
@RequestMapping("/admin/dish")
@RestController
@Slf4j
@Api(tags = "菜品控制器")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @ApiOperation("新增菜品")
    @PostMapping("")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品{}", dishDTO.getName());

        dishService.addDish(dishDTO);

        return Result.success();
    }


    /**
     * 页面回显接口
     * @param page
     * @param pageSize
     * @param name
     * @param categoryId
     * @param status
     * @return
     */
    @ApiOperation("页面回显接口")
    @GetMapping("/page")
    public Result showDish(int page, int pageSize, String name, Integer categoryId, Integer status){

        DishPageQueryDTO dishPageQueryDTO = DishPageQueryDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .name(name)
                .categoryId(categoryId)
                .status(status)
                .build();

        PageResult pageResult = dishService.showDish(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除菜品接口
     * @param ids
     * @return
     */
    @ApiOperation("删除菜品接口")
    @DeleteMapping("")
    public Result deleteDish(@RequestParam List<Integer> ids){

        log.info("删除菜品接口{}",ids);

        dishService.deleteDish(ids);

        return Result.success();
    }

    /**
     * 根据id查找菜品接口
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查找菜品接口")
    public Result findDishById(@PathVariable long id){

        log.info("查找id={}菜品接口",id);
        DishVO dishReturn = dishService.findDishById(id);

        return Result.success(dishReturn);
    }


    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("修改菜品接口")
    @PutMapping("")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("正在修改菜品{}",dishDTO.getName());

        dishService.updateDish(dishDTO);

        return Result.success();
    }

    /**
     * 修改菜品状态接口
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态接口")
    public Result updateDishStatus(@PathVariable Integer status,@RequestParam Long id){

        dishService.updateDishStatus(status,id);

        return Result.success();
    }

    @GetMapping("/list")
    public Result listByCategoryId(Long categoryId){

       List<Dish> dishVO = dishService.listByCategoryId(categoryId);

        return Result.success(dishVO);
    }

}
