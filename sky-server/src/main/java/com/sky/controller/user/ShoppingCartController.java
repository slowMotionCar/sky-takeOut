package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description ShoppingCart
 * @Author Zhilin
 * @Date 2023-10-05
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车相关接口")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    /**
     * 添加购物车接口
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addToCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车{}",shoppingCartDTO);
        shoppingCartService.addToCart(shoppingCartDTO);
        return Result.success();
    }


    /**
     * 显示购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("显示购物车")
    public Result list(){
        log.info("显示购物车");
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    /**
     * 删除购物车
     * @return
     */
    @ApiOperation("删除购物车")
    @DeleteMapping("/clean")
    public Result delete(){
        log.info("删除购物车");
        shoppingCartService.delete();
        return Result.success();
    }

}
