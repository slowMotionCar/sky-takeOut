package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserLoginMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserLoginService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description UserLoginControllerServiceImpl
 * @Author Zhilin
 * @Date 2023-09-30
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserLoginMapper userLoginMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //调用微信接口生成openID

        String openID = findOpenID(userLoginDTO);

        //查找数据库是否存在, 如果没有存入数据库
        if(openID == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userLoginMapper.getUserbyOpenId(openID);

        if(user == null){
            User userInput = User.builder()
                    .openid(openID)
                    .createTime(LocalDateTime.now())
                    .build();
            userLoginMapper.insertUser(userInput);

            return userInput;
        }


        return user;
    }

    private String findOpenID(UserLoginDTO userLoginDTO) {
        String jsCode = userLoginDTO.getCode();
        Map map = new HashMap();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",jsCode);
        map.put("grant_type","authorization_code");

        //这是微信获取openid的接口
        String json = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", map);
        JSONObject jsonObject = JSON.parseObject(json);
        //jsonObj此时已经成为了Map
        String openId = (String) jsonObject.get("openid");
        return openId;
    }
}
