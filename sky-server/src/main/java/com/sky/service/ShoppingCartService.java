package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @Description ShoppingCartService
 * @Author Zhilin
 * @Date 2023-10-05
 */

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addToCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 显示购物车
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 删除购物车
     */
    void delete();

}
