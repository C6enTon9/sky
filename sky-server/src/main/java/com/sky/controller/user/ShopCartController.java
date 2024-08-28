package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "购物车相关接口")
@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShopCartController {

    @Autowired
    private ShopCartService shopCartService;


    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {

        log.info("添加购物车：{}", shoppingCartDTO);

        shopCartService.addShoppingCart(shoppingCartDTO);

        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询购物车")
    public Result<List<ShoppingCart>> list(){

        log.info("查询购物车...");


        List<ShoppingCart> list = shopCartService.showShoppingCart();

        return Result.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result deleteAllShoppingCart(){

        log.info("清空购物车...");

        shopCartService.deleteAllShoppingCart();

        return Result.success();
    }


    /**
     * 减少购物车菜品数量
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation("减少购物车菜品数量")
    public Result subShoppingCart(ShoppingCartDTO shoppingCartDTO){

        log.info("减少购物车菜品数量...");

        shopCartService.subShoppingCart(shoppingCartDTO);

        return Result.success();
    }
}
