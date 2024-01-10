package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

/**
 * @Description UserLoginControllerService
 * @Author Zhilin
 * @Date 2023-09-30
 */
public interface UserLoginService {
    User login(UserLoginDTO userLoginDTO);
}
