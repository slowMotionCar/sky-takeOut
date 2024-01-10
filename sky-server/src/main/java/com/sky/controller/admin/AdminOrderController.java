package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description OrderController
 * @Author Zhilin
 * @Date 2023-10-07
 */
@RestController
@RequestMapping("/admin/order")
@Api(tags = "商家端订单管理接口模块")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单分页查询")
    public Result conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {

        log.info("订单分页查询{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearchOrder(ordersPageQueryDTO);

        return Result.success(pageResult);

    }

    /**
     * 各个状态订单数据统计
     *
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态订单数据统计接口")
    public Result orderStatistic() {
        log.info("各个状态订单数据统计接口");
        OrderStatisticsVO orderStatisticsVO = orderService.orderStatistic();
        return Result.success(orderStatisticsVO);
    }


    /**
     * 查询订单详情
     *
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result detailByOrderId(@PathVariable Long id) {
        log.info("查询{}订单详情", id);

        OrderVO orderVO = orderService.getOrderByOrderId(id);

        return Result.success(orderVO);
    }

    /**
     * 商家接单接口
     * @return
     */
    @ApiOperation("商家接单接口")
    @PutMapping("/confirm")
    public Result orderConfirmed(@RequestBody OrdersDTO ordersDTO) {
        log.info("商家接单{}", ordersDTO.getId());
        Long id = ordersDTO.getId();
        orderService.orderConfirmed(id);

        return Result.success();
    }

    /**
     * 商家拒单
     *
     * @param rejectionDTO
     * @return
     */
    @ApiOperation("商家拒单接口")
    @PutMapping("/rejection")
    public Result orderRejection(@RequestBody OrdersRejectionDTO rejectionDTO) {
        log.info("商家拒单{}", rejectionDTO);

        orderService.orderRejection(rejectionDTO);

        return Result.success();
    }

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @ApiOperation("商家取消订单接口")
    @PutMapping("/cancel")
    public Result cancelOrderByAdmin(@RequestBody OrdersCancelDTO ordersCancelDTO) {

        log.info("商家取消订单{}", ordersCancelDTO);
        orderService.cancelOrderByAdmin(ordersCancelDTO);

        return Result.success();
    }

    /**
     * 派送订单
     *
     * @param id
     * @return
     */
    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable Long id) {
        log.info("商家取消订单");
        orderService.deliveryOrder(id);

        return Result.success();
    }


    /**
     * 完成订单
     * @param id
     * @return
     */
    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable Long id){
        log.info("完成订单{}",id);
        orderService.completeOrder(id);
        return Result.success();
    }
}
