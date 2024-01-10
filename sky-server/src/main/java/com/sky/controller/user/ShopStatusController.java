package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ShopStatusController
 * @Author Zhilin
 * @Date 2023-09-26
 */

@Api("获取商店状态相关接口")
@RestController(value = "UserShopStatusController")
@Slf4j
@RequestMapping("/user/shop")
public class ShopStatusController {

    private final RedisTemplate redisTemplate;

    public ShopStatusController(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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
            status = 0;
        }

        return Result.success(status);
    }
}
