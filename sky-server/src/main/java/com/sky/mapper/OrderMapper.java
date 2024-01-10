package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description OrderMapper
 * @Author Zhilin
 * @Date 2023-10-05
 */
@Mapper
public interface OrderMapper {
    void add(Orders orders);


    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumberAndUserId(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    // @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> findOrderByTimeAndStatus(Orders orders);

    Orders getOrderByUserId(Long currentId);

    Orders getOrderByUserIdAndNumber(String outTradeNo, Long currentId);

    List<Orders> findOrderByUserAndStatus(OrdersPageQueryDTO ordersPageQueryDTO);

    Orders findOrderByOrderId(Long id);

    List<Orders> conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    Integer countConfirmed();

    Integer countDeliveryInProgress();

    Integer countToBeConfirmed();

    Double sumByMap(Map map);

    Integer countByMap(Map map);

    Integer customersStatistic(Map map);

    List findTop10(Map map);
}
