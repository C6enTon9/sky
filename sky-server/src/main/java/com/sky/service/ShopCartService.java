package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShopCartService {


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);


    /**
     * 查询购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();


    /**
     * 清空购物车
     */
    void deleteAllShoppingCart();


    /**
     * 减少购物车菜品数量
     */
    void subShoppingCart(ShoppingCartDTO  shoppingCartDTO);
}
