package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description OrderController
 * @Author Zhilin
 * @Date 2023-10-05
 */
@RestController
@RequestMapping("/user/order")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "订单提交相关接口")
public class OrderController {

    private final OrderService orderService;

    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("提交订单{}", ordersSubmitDTO );
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }


    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("分页查询历史订单")
    public Result historyOrderDisplay(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询历史订单{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.historyOrderDisplay(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 催单接口
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单接口")
    public Result orderReminder(@PathVariable Long id){
        log.info("{}用户催单",id);
        orderService.orderReminder(id);
        return Result.success();
    }

    /**
     * 查询订单详情接口
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情接口")
    public Result queryOrderDetail(@PathVariable Long id){
        log.info("查询订单{}详情接口",id);
        OrderVO orderVO = orderService.getOrderByOrderId(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单接口
     * @return
     */
    @ApiOperation("取消订单接口")
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        String refund = orderService.cancelOrder(id);
        System.out.println(refund);
        return Result.success(refund);
    }

    /**
     * 再下一单
     * @param id
     * @return
     */
    @ApiOperation("再下一单")
    @PostMapping("/repetition/{id}")
    public Result orderRepetition(@PathVariable Long id){
        log.info("订单{}再来一单",id);
        orderService.orderRepetition(id);
        return Result.success();
        }
}
