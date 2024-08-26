package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";


@Autowired
private WeChatProperties weChatProperties;
@Autowired
private UserMapper userMapper;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User vxLogin(UserLoginDTO userLoginDTO) {

        //调用微信提供的接口服务，传入code 返回微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空 若为null则返回登录失败异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户
        User user = userMapper.selectByOpenid(openid);
        //若为新用户则插入
        if(user == null){
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }
        return user;
    }

    private String getOpenid(String code) {
        //调用微信提供的接口服务，传入code 返回微信用户的openid
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
