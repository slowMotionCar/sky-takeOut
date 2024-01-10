package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description UserLoginMapper
 * @Author Zhilin
 * @Date 2023-09-30
 */
@Mapper
public interface UserLoginMapper {
    User getUserbyOpenId(String openID);

    void insertUser(User userInput);
}
