package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * @Description OrderService
 * @Author Zhilin
 * @Date 2023-10-05
 */
public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    Orders getOrderByUserId(Long userId);

    Orders getOrderByUserIdAndNumber(String outTradeNo, Long currentId);

    void update(Orders orderUpdate);

    PageResult historyOrderDisplay(OrdersPageQueryDTO ordersPageQueryDTO);

    void orderReminder(Long id);

    OrderVO getOrderByOrderId(Long id);

    String cancelOrder(Long id);

    void orderRepetition(Long id);

    PageResult conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO orderStatistic();

    void orderConfirmed(Long id);

    void orderRejection(OrdersRejectionDTO rejectionDTO);

    void cancelOrderByAdmin(OrdersCancelDTO ordersCancelDTO);

    void deliveryOrder(Long id);

    void completeOrder(Long id);
}


