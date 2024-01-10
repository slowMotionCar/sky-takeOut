package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserLoginService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description UserLoginController
 * @Author Zhilin
 * @Date 2023-09-30
 */
@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Api(tags = "用户登陆接口相关")
@Slf4j
public class UserLoginController {

    public final UserLoginService userLoginService;
    public final JwtProperties jwtProperties;

    @PostMapping ("/login")
    @ApiOperation("用户登陆接口")
    public Result login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登录{}",userLoginDTO);

        User user = userLoginService.login(userLoginDTO);

        //下发令牌
        Map map = new HashMap();
        map.put(JwtClaimsConstant.USER_ID,user.getId());
        map.put("openId",user.getOpenid());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), map);

        //封装UserLoginVo
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(jwt)
                .build();

        System.out.println(userLoginVO);
        return Result.success(userLoginVO);
    }

}
