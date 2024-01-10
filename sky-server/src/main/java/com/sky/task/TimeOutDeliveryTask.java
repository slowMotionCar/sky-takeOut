package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description TimeOutDeliveryTask
 * @Author Zhilin
 * @Date 2023-10-06
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TimeOutDeliveryTask {

    private final OrderMapper orderMapper;

    /**
     * 凌晨1点检查是否有昨天的还在派送的订单
     */

    // @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0 0 1 * * ?")
    public void TimeOutDeliveryTask() {
        // 检查是否存在派送中的订单
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);
        Orders orders = new Orders();
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orders.setOrderTime(localDateTime);

        List<Orders> orderFind = orderMapper.findOrderByTimeAndStatus(orders);
        log.info("找到{}个在派送中的订单", orderFind.size());
        // 修改状态为已完成


        if (orderFind != null && orderFind.size() > 0) {
            Orders orderFinished = new Orders();
            orderFinished.setStatus(Orders.COMPLETED);
            orderFinished.setDeliveryTime(LocalDateTime.now());
            orderFind.forEach((order) -> {
                order.setDeliveryTime(orderFinished.getDeliveryTime());
                order.setStatus(orderFinished.getStatus());
                orderMapper.update(order);
            });
        }
    }


}
