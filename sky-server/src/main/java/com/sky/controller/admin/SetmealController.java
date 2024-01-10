package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description SetmealController
 * @Author Zhilin
 * @Date 2023-09-25
 */
@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("新增套餐")
    @PostMapping("")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {

        setmealService.addSetmeal(setmealDTO);

        return Result.success();
    }


    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("分页查询接口")
    @GetMapping("/page")
    public Result setmealQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        System.out.println("我来了");
        PageResult pageResult = setmealService.setmealQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @ApiOperation("删除套餐")
    @DeleteMapping("")
    public Result deleteSetmeal(@RequestParam List<Long> ids){

        setmealService.deleteSetmeal(ids);

        return Result.success();
    }

    /**
     * 回显
     * @param id
     * @return
     */
    @ApiOperation("回显")
    @GetMapping("/{id}")
    public Result findById(@PathVariable long id){

        SetmealVO setmealVO = setmealService.findById(id);

        return Result.success(setmealVO);
    }


    /**
     * 修改套餐接口
     * @param setmealDTO
     * @return
     */
    @ApiOperation("修改套餐接口")
    @PutMapping("")
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 起售停售套餐
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售套餐")
    public Result changeStatus(@PathVariable Integer status, long id){


        setmealService.changeStatus(status, id);

        return Result.success();
    }

}
