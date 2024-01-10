package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description UserMapper
 * @Author Zhilin
 * @Date 2023-10-05
 */
@Mapper
public interface UserMapper {

    @Select("select * from user where id = #{userId} ")
    User getById(Long userId);
}
