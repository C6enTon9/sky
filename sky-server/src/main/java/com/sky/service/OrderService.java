package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);



    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult historyOrder(Integer page, Integer pageSize, Integer status);


    /**
     *查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Integer id);


    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);


    /**
     * 管理端分页订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionPageQuerySearch(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 统计各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO countStatistics();


    /**
     * 查询订单详情管理端
     * @param id
     * @return
     */
    OrderVO getOrderVOById(Integer id);


    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    void acceptOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);


    /**
     * 完成订单
     * @param id
     */
    void completeOrder(Integer id);


    /**
     * 派送订单
     * @param id
     */
    void deliveryOrder(Long id);


    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO);
}
