package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

/**
 * @Description WorkspaceService
 * @Author Zhilin
 * @Date 2023-10-09
 */


public interface WorkspaceService {
    BusinessDataVO businessData();

    OrderOverViewVO orderData();

    SetmealOverViewVO setmealData();

    DishOverViewVO dishData();
}
