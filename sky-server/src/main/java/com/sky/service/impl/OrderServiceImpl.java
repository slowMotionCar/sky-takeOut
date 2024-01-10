package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description OrderServiceImpl
 * @Author Zhilin
 * @Date 2023-10-05
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserMapper userMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final WebSocketServer webSocketServer;

    @Value("${sky.shop.address}")
    private String shopAddress;


    @Value("${sky.baidu.ak}")
    private String ak;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        Long userId = BaseContext.getCurrentId();

        // 判断是否错误
        // 地址为空

        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // 判断地址是否大于五公里
        // 百度地图接口
        // https://lbsyun.baidu.com/faq/api?title=webapi/guide/webservice-geocoding-base
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        findGPS(address);


        // 购物车为空
        // ShoppingCart shoppingCart = new ShoppingCart();
        // shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartReturn = shoppingCartMapper.listAll(userId);
        if (shoppingCartReturn == null) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 填写表order
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        orders.setNumber(uuid);
        // 这里应该填写Orders.PendingPayment, 由于无法链接企业支付账号,用TOBE_CONFIRMED进行测试
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(0);

        User user = userMapper.getById(userId);
        orders.setUserName(user.getName());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName());

        orderMapper.add(orders);

        // 填写表orderdetails


        List<ShoppingCart> shoppingCarts = shoppingCartMapper.listAll(userId);

        List<OrderDetail> orderDetails = new ArrayList<>();
        shoppingCarts.forEach((shoppingCartItem) -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCartItem, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
            System.out.println("shopppingCCCCC" + orderDetails);
        });

        orderDetailMapper.insertBatch(orderDetails);

        // 删除购物车
        shoppingCartMapper.delete(userId);
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

        return orderSubmitVO;
    }

    private void findGPS(String userAddress) {
        Map map = new HashMap();
        map.put("address", userAddress);
        map.put("ak", ak);
        map.put("output", "json");
        String url = "https://api.map.baidu.com/geocoding/v3";
        String result = HttpClientUtil.doGet(url, map);
        System.out.println("获取经纬度!" + result);

        // 解析result
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("status");
        // 检查是否返回陈公公
        if (!status.equals("0")) {
            throw new BaseException("地址未找到");
        }
        // 返回则解析位置
        JSONObject Location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lng = Location.getString("lng");
        String lat = Location.getString("lat");


        // 店铺位置
        map.put("address", shopAddress);
        String shopReturn = HttpClientUtil.doGet(url, map);
        System.out.println("获取经纬度!" + shopReturn);
        JSONObject shopObj = JSONObject.parseObject(shopReturn);
        JSONObject shopObj2 = shopObj.getJSONObject("result").getJSONObject("location");
        String lngShop = shopObj2.getString("lng");
        String latShop = shopObj2.getString("lat");


        // 计算间隔距离
        // 利用百度地图路线计算
        // https://lbsyun.baidu.com/faq/api?title=webapi/guide/webservice-lwrouteplanapi/cycling

        String originLngLat = latShop + "," + lngShop;
        String destinationLngLat = lat + "," + lng;
        System.out.println("start" + originLngLat + "  over" + destinationLngLat);
        String urlRoute = "https://api.map.baidu.com/directionlite/v1/riding";
        Map route = new HashMap<>();
        route.put("ak", ak);
        route.put("origin", originLngLat);
        route.put("destination", destinationLngLat);
        route.put("steps_info","0");
        String BaiduRoutes = HttpClientUtil.doGet(urlRoute, route);
        System.out.println("ROUTE!!" + BaiduRoutes);

        // 无效情况
        JSONObject jsonObject1 = JSONObject.parseObject(BaiduRoutes);
        System.out.println("json1:"+jsonObject1);
        String status1 = jsonObject1.getString("status");
        if (!status1.equals("0")) {

            throw new BaseException("无效输入, 无返回值");
        }
        // 可以的话获取长度
        JSONArray jsonObject2 = jsonObject1.getJSONObject("result").getJSONArray("routes");
        System.out.println(jsonObject2);
        JSONObject distanceObj = (JSONObject)(jsonObject2.get(0));
        Integer distance = (Integer) distanceObj.get("distance");

        // 长于5km不派送
        if (distance > 5000) {
            throw new BaseException("距离过长不允下单");
        }

    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        // 调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), // 商户订单号
                new BigDecimal(0.01), // 支付金额，单位 元
                "苍穹外卖订单", // 商品描述
                user.getOpenid() // 微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }


    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 更具用户id获取对应的order
     *
     * @param currentId
     * @return
     */
    @Override
    public Orders getOrderByUserId(Long currentId) {
        Orders order = orderMapper.getOrderByUserId(currentId);
        return order;
    }

    /**
     * 根据ID和订单号获取对应的订单
     *
     * @param outTradeNo
     * @param currentId
     * @return
     */
    @Override
    public Orders getOrderByUserIdAndNumber(String outTradeNo, Long currentId) {
        Orders order = orderMapper.getOrderByUserIdAndNumber(outTradeNo, currentId);
        return order;
    }

    /**
     * 更新订单
     *
     * @param orderUpdate
     */
    @Override
    public void update(Orders orderUpdate) {
        orderMapper.update(orderUpdate);
    }

    /**
     * 分页查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrderDisplay(OrdersPageQueryDTO ordersPageQueryDTO) {

        // 开启pagehelper
        int page = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        // Integer status = ordersPageQueryDTO.getStatus();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(page, pageSize);

        List<Orders> lists = orderMapper.findOrderByUserAndStatus(ordersPageQueryDTO);
        Page page1 = (Page) lists;


        // Todo 这里可以使用 Stream API 和 Lambda 表达式来简化代码
        /*
        List<OrderVO> listVO = new ArrayList<>();
        lists.forEach((list)->{
            OrderVO orderVOTemp = new OrderVO();
            BeanUtils.copyProperties(list,orderVOTemp);
            //添加菜品详细
            //获取订单id
            Long orderId = list.getId();
            List<OrderDetail> listOrderDetail = orderDetailMapper.getOrderDetailByOrderId(orderId);
            orderVOTemp.setOrderDetailList(listOrderDetail);
            listVO.add(orderVOTemp);
        });
        */
        // 利用stream自动映射并装配
        List<Object> listVO = lists.stream().map((list) -> {
            OrderVO orderVOTemp = new OrderVO();
            BeanUtils.copyProperties(list, orderVOTemp);
            // 获取订单id
            Long orderId = list.getId();
            List<OrderDetail> listOrderDetail = orderDetailMapper.getOrderDetailByOrderId(orderId);
            orderVOTemp.setOrderDetailList(listOrderDetail);
            // 自动封装多个返回值orderVOTemp进list
            return orderVOTemp;
        }).collect(Collectors.toList());

        return PageResult.builder()
                .total(page1.getTotal())
                .records(listVO)
                .build();
    }

    /**
     * 催单
     *
     * @param id
     */
    @Override
    public void orderReminder(Long id) {

        // 判断订单是否存在
        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);

        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号为: " + orderByOrderId.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderByOrderId(Long id) {

        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);
        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderByOrderId, orderVO);
        List<OrderDetail> orderDetailByOrderId = orderDetailMapper.getOrderDetailByOrderId(id);
        orderVO.setOrderDetailList(orderDetailByOrderId);
        return orderVO;
    }

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    @Override
    public String cancelOrder(Long id) {
        Orders orderCancelled = orderMapper.findOrderByOrderId(id);
        if (orderCancelled == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        try {
            // 退钱
            String refund = weChatPayUtil.refund(orderCancelled.getNumber(), orderCancelled.getNumber(), orderCancelled.getAmount(), orderCancelled.getAmount());
            // 设置状态
            orderCancelled.setStatus(Orders.CANCELLED);
            orderCancelled.setCancelTime(LocalDateTime.now());
            orderCancelled.setCancelReason(MessageConstant.ORDER_REFUND);
            orderCancelled.setPayStatus(Orders.REFUND);
            orderMapper.update(orderCancelled);
            return refund;
        } catch (Exception e) {
            throw new BaseException("Refund Failed.");
        }
    }

    /**
     * 再下一单
     *
     * @param id
     */
    @Override
    public void orderRepetition(Long id) {
        // 添加订单
        log.info("开始添加");
        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);
        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders orderRep = new Orders();
        // 把已有数据添加入新Bean
        BeanUtils.copyProperties(orderByOrderId, orderRep);
        // 初始化新Bean
        orderRep.setNumber(UUID.randomUUID().toString().replace("-", ""));
        orderRep.setUserId(BaseContext.getCurrentId());
        orderRep.setOrderTime(LocalDateTime.now());
        orderRep.setStatus(Orders.TO_BE_CONFIRMED);
        orderRep.setPayStatus(Orders.UN_PAID);
        orderMapper.add(orderRep);
        // 添加订单详细
        List<OrderDetail> orderDetailByOrderId = orderDetailMapper.getOrderDetailByOrderId(id);
        List<OrderDetail> collect = orderDetailByOrderId.stream().map((orderDetail) -> {
            orderDetail.setOrderId(orderRep.getId());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailMapper.insertBatch(collect);
    }

    /**
     * 分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {

        // 创建pagehelper
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        List<Orders> orderLists = orderMapper.conditionSearchOrder(ordersPageQueryDTO);
        if (orderLists == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Page page = (Page) orderLists;

        // 封装orderdetails
        List<OrderVO> orderVOs = orderLists.stream().map((order) -> {
            OrderVO orderVOTemp = new OrderVO();
            BeanUtils.copyProperties(order, orderVOTemp);
            // 根据orderID查询orderDetail
            List<OrderDetail> orderDetailByOrderId = orderDetailMapper.getOrderDetailByOrderId(order.getId());

            // 拼接字符串
            StringBuilder stringBuilder = new StringBuilder();
            for (OrderDetail orderDetail : orderDetailByOrderId) {
                stringBuilder.append(orderDetail.getName());
                stringBuilder.append("*");
                stringBuilder.append(orderDetail.getNumber());
                stringBuilder.append(" ");

            }
            String nameString = stringBuilder.toString();
            // 打包输出
            orderVOTemp.setOrderDetailList(orderDetailByOrderId);
            orderVOTemp.setOrderDishes(nameString);
            return orderVOTemp;
        }).collect(Collectors.toList());


        return PageResult.builder()
                .total(page.getTotal())
                .records(orderVOs)
                .build();
    }

    /**
     * 查询各个状态订单数量
     *
     * @return
     */
    @Override
    public OrderStatisticsVO orderStatistic() {

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Integer confirmed = orderMapper.countConfirmed();
        Integer deliveryInProgress = orderMapper.countDeliveryInProgress();
        Integer toBeConfirmed = orderMapper.countToBeConfirmed();

        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);

        return orderStatisticsVO;
    }

    /**
     * 商家接单
     *
     * @param id
     */
    @Override
    public void orderConfirmed(Long id) {
        // 根据id查数据
        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);
        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 修改状态  补充字段
        orderByOrderId.setStatus(Orders.CONFIRMED);
        // 更新
        orderMapper.update(orderByOrderId);
    }

    @Override
    public void orderRejection(OrdersRejectionDTO rejectionDTO) {
        Long id = rejectionDTO.getId();
        String rejectionReason = rejectionDTO.getRejectionReason();
        // 根据id获取order
        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);
        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 修改order的状态为拒单
        orderByOrderId.setStatus(Orders.CANCELLED);
        orderByOrderId.setCancelTime(LocalDateTime.now());
        orderByOrderId.setRejectionReason(rejectionReason);
        // 更新order
        orderMapper.update(orderByOrderId);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @Override
    public void cancelOrderByAdmin(OrdersCancelDTO ordersCancelDTO) {
        Long id = ordersCancelDTO.getId();
        Orders orderCancelled = orderMapper.findOrderByOrderId(id);
        if (orderCancelled == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 设置状态
        orderCancelled.setStatus(Orders.CANCELLED);
        orderCancelled.setCancelTime(LocalDateTime.now());
        orderCancelled.setCancelReason(ordersCancelDTO.getCancelReason());
        orderCancelled.setPayStatus(Orders.REFUND);
        orderMapper.update(orderCancelled);
        try {
            // 退钱
            String refund = weChatPayUtil.refund(orderCancelled.getNumber(), orderCancelled.getNumber(), orderCancelled.getAmount(), orderCancelled.getAmount());


        } catch (Exception e) {
            throw new BaseException("Refund Failed.");
        }
    }

    /**
     * 派送订单
     *
     * @param id
     */
    @Override
    public void deliveryOrder(Long id) {
        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);
        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orderByOrderId.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orderByOrderId);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    @Override
    public void completeOrder(Long id) {
        Orders orderByOrderId = orderMapper.findOrderByOrderId(id);
        if (orderByOrderId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orderByOrderId.setStatus(Orders.COMPLETED);
        orderByOrderId.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orderByOrderId);
    }

}
