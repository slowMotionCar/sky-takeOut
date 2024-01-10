package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description WorkspaceController
 * @Author Zhilin
 * @Date 2023-10-09
 */

@Slf4j
@RequiredArgsConstructor
@Api(tags = "工作台相关接口")
@RestController
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    /**
     * 今日运营数据接口
     *
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("今日运营数据接口")
    public Result businessData() {
        log.info("今日运营数据!!!");
        BusinessDataVO businessDataVO = workspaceService.businessData();
        return Result.success(businessDataVO);
    }

    /**
     * 订单管理数据
     *
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("订单管理数据接口")
    public Result orderData() {
        log.info("订单管理数据");

        OrderOverViewVO orderOverViewVO = workspaceService.orderData();

        return Result.success(orderOverViewVO);
    }

    /**
     * 套餐总览
     *
     * @return
     */
    @ApiOperation("套餐总览接口")
    @GetMapping("/overviewSetmeals")
    public Result setmealData() {
        log.info("套餐总览接口");

        SetmealOverViewVO setmealOverViewVO = workspaceService.setmealData();

        return Result.success(setmealOverViewVO);
    }

    /**
     * 菜品总览
     *
     * @return
     */
    @ApiOperation("菜品总览接口")
    @GetMapping("/overviewDishes")
    public Result dishData() {
        log.info("菜品总览接口");

        DishOverViewVO dishOverViewVO = workspaceService.dishData();

        return Result.success(dishOverViewVO);
    }
}
