package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/admin/order")
@RestController("adminOrderController")
@Api(tags = "管理端订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("分页订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionPageQuerySearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页订单搜索");

        PageResult pageResult = orderService.conditionPageQuerySearch(ordersPageQueryDTO);

        return Result.success(pageResult);
    }


    @GetMapping("/statistics")
    @ApiOperation("/统计各个状态的订单数量统计")
    public Result<OrderStatisticsVO> countStatistics(){
        log.info("统计各个状态的订单数量统计...");

        return Result.success(orderService.countStatistics());
    }


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Integer id){

        log.info("查询订单详情:{}",id);

        OrderVO orderVO = orderService.getOrderVOById(id);

        return Result.success(orderVO);
    }


    /**
     * 商家接单
     * @param ordersConfirmDTO
     * @return
     */
    @ApiOperation("商家接单")
    @PutMapping("/confirm")
    public Result acceptOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){

        log.info("商家接单：{}",ordersConfirmDTO);

        orderService.acceptOrder(ordersConfirmDTO);
        return Result.success();
    }


    /**
     * 商家接单
     * @param ordersRejectionDTO
     * @return
     */
    @ApiOperation("商家拒单")
    @PutMapping("/rejection")
    public Result rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){

        log.info("商家拒单：{}",ordersRejectionDTO);

        orderService.rejectionOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result compeleteOrder(@PathVariable Integer id){

        log.info("完成订单：{}",id);

        orderService.completeOrder(id);

        return Result.success();
    }


    /**
     * 派送订单
     * @param id
     * @return
     */
    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable Long id){

        log.info("派送订单：{}",id);

        orderService.deliveryOrder(id);

        return Result.success();
    }


    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单：{}",ordersCancelDTO);

        orderService.cancelOrder(ordersCancelDTO);

        return Result.success();
    }
}
