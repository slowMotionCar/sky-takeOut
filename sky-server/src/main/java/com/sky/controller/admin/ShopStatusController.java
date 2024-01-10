package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description ShopStatusController
 * @Author Zhilin
 * @Date 2023-09-26
 */
@RestController
@Api("商店状态相关接口")
@Slf4j
@RequestMapping("/admin/shop")
@RequiredArgsConstructor
public class ShopStatusController {

    // @Resource
    private final RedisTemplate redisTemplate;


    /**
     * 设置商店状态
     * @param status
     * @return
     */
    @ApiOperation("设置商店状态接口")
    @PutMapping("/{status}")
    public Result changeStatus(@PathVariable Integer status){

        log.info("设置商店状态为{}",status);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("SHOP_STATUS", status);
        return Result.success();
    }


    /**
     * 获取商店状态
     * @return
     */
    @ApiOperation("获取商店状态接口")
    @GetMapping("/status")
    public Result displayStatus(){

        log.info("获取商店状态");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get("SHOP_STATUS");
        if (status==null){
            valueOperations.set("SHOP_STATUS",0);
        }

        return Result.success(status);
    }

}
