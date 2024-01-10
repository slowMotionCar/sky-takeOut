package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description WorkspaceServiceImpl
 * @Author Zhilin
 * @Date 2023-10-09
 */
@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private DishMapper dishMapper;

    @Override
    public BusinessDataVO businessData() {
        // 获取当天时间
        LocalDate localDate = LocalDate.now();
        // 当天最早 和最晚
        LocalDateTime begin = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(localDate, LocalTime.MAX);
        // 统计订单总数
        Map map = new HashMap<>();
        map.put("beginTime", begin);
        map.put("endTime", end);
        Integer totalOrder = orderMapper.countByMap(map);
        // 统计新增客户
        Integer customerCount = orderMapper.customersStatistic(map);
        // 统计有效订单总数
        map.put("status", Orders.COMPLETED);
        Integer finishedOrder = orderMapper.countByMap(map);
        // 统计营业额
        Double totalAmount = orderMapper.sumByMap(map);
        // 计算订单完成率
        Double orderFinishedRate = finishedOrder * 1.0 / totalOrder;
        // 计算平均客单率
        Double amountPerOrder = totalAmount * 1.0 / finishedOrder;
        return BusinessDataVO.builder()
                .newUsers(customerCount)
                .orderCompletionRate(orderFinishedRate)
                .unitPrice(amountPerOrder)
                .validOrderCount(finishedOrder)
                .turnover(totalAmount)
                .build();
    }

    /**
     * 订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO orderData() {

        // 搜索全部订单数量
        Integer totalOrder = orderMapper.countByMap(new HashMap<>());
        // 搜索等待接单订单数量
        Map waitOrderMap = new HashMap<>();
        waitOrderMap.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitOrder = orderMapper.countByMap(waitOrderMap);
        // 搜索待派送订单数量
        Map deliveredOrdersMap = new HashMap<>();
        deliveredOrdersMap.put("status", Orders.CONFIRMED);
        Integer deliveredOrder = orderMapper.countByMap(deliveredOrdersMap);
        // 搜索已完成订单数量
        Map finishedOrdersMap = new HashMap<>();
        finishedOrdersMap.put("status", Orders.COMPLETED);
        Integer finishedOrder = orderMapper.countByMap(finishedOrdersMap);
        // 搜索已取消订单数量
        Map canceledOrdersMap = new HashMap<>();
        canceledOrdersMap.put("status", Orders.CONFIRMED);
        Integer canceledOrder = orderMapper.countByMap(canceledOrdersMap);


        return OrderOverViewVO.builder()
                .allOrders(totalOrder)
                .cancelledOrders(canceledOrder)
                .completedOrders(finishedOrder)
                .deliveredOrders(deliveredOrder)
                .waitingOrders(waitOrder)
                .build();
    }

    /**
     * 套餐总览接口
     * @return
     */
    @Override
    public SetmealOverViewVO setmealData() {

        //查询起售套餐
        Integer enableSetmeal = setmealMapper.countByStatus(StatusConstant.ENABLE);
        //查询停售套餐
        Integer disableSetmeal = setmealMapper.countByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(enableSetmeal)
                .discontinued(disableSetmeal)
                .build();
    }

    @Override
    public DishOverViewVO dishData() {

        //查询起售菜品
        Integer enableDish = dishMapper.countByStatus(StatusConstant.ENABLE);
        //查询停售菜品
        Integer disableDish = dishMapper.countByStatus(StatusConstant.DISABLE);
        return DishOverViewVO.builder()
                .discontinued(disableDish)
                .sold(enableDish)
                .build();
    }
}
