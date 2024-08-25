package com.sky.service;

public interface ShopService {

    /**
     * 修改店铺营业状态
     * @param status
     */
    void setStatus(Integer status);

    /**
     * 查询店铺营业状态
     * @return
     */
    Integer getStatus();
}
