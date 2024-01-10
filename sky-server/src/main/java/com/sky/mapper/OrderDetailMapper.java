package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description OrderDetailMapper
 * @Author Zhilin
 * @Date 2023-10-05
 */
@Mapper
public interface OrderDetailMapper {

    void insertBatch(List<OrderDetail> orderDetails);

    List<OrderDetail> getOrderDetailByOrderId(Long orderId);

    List<String> getNameByOrderId(Long id);
}
