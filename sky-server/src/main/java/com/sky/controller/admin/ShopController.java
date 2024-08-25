package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 修改店铺营业状态
     * @param status
     * @return
     */
    @ApiOperation("修改店铺营业信息")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable("status") Integer status){

        log.info("修改店铺营业状态：{}",status == 1 ? "营业中" : "打烊中");

        shopService.setStatus(status);

        return Result.success();
    }


    /**
     * 查询店铺营业状态
     * @return
     */
    @ApiOperation("查询店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){

        Integer status = shopService.getStatus();

        log.info("查询店铺营业状态为：{}",status);

        return Result.success(status);
    }
}
