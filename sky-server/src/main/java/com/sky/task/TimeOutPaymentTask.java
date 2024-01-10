package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description TimeOutPaymentTask
 * @Author Zhilin
 * @Date 2023-10-06
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TimeOutPaymentTask {

    private final OrderMapper orderMapper;


    /**
     * 检查是否有超过15分钟未支付订单
     */
    // @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void timeOutPaymentTask() {
        log.info("检查是否有超过15分钟未支付订单");
        // 查找15分钟未支付订单
        Orders orders = new Orders();
        // orders.setUserId(currentId);
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(15);
        orders.setOrderTime(localDateTime);
        orders.setStatus(Orders.PENDING_PAYMENT);
        List<Orders> lists = orderMapper.findOrderByTimeAndStatus(orders);
        log.info("找到了{}个超时未支付订单", lists.size());
        // 修改订单状态设置为已取消
        Orders orderCancelled = new Orders();
        orderCancelled.setStatus(Orders.CANCELLED);
        orderCancelled.setCancelReason(MessageConstant.ORDER_TIMEOUT_CANCEL);
        orderCancelled.setCancelTime(LocalDateTime.now());

        if (lists != null && lists.size() > 0) {
            // Todo 这里可以用到updateBatch批量更新会更好一点
            lists.forEach((list) -> {
                list.setStatus(orderCancelled.getStatus());
                list.setCancelTime(orderCancelled.getCancelTime());
                list.setCancelReason(orderCancelled.getCancelReason());
                orderMapper.update(list);
            });
        }
    }
}
