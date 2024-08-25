package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    public static final String key = "SHOP_STATUS";


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 修改店铺营业状态
     * @param status
     */
    @Override
    public void setStatus(Integer status) {

        redisTemplate.opsForValue().set("key", status);

    }

    /**
     * 查询店铺营业状态
     * @return
     */
    @Override
    public Integer getStatus() {

        Integer status = (Integer) redisTemplate.opsForValue().get("key");

        return status;
    }
}
