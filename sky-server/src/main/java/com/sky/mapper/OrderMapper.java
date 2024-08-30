package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders
     */
    public void insert(Orders orders);


    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 分页查询订单信息
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQueryHistoryOrder(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据订单id查询订单信息
     * @param id
     * @return
     */
    Orders getById(Integer id);


    /**
     * 统计各个订单状态的数量
     * @param status
     * @return
     */
    @Select("select count(0) from sky_take_out.orders where status = #{status}")
    Integer countStatistics(Integer status);


    /**
     * 根据订单状态以及订单下单时间查询
     * @param status
     * @param localDateTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{localDateTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime localDateTime);


//    /**
//     * 根据订单状态查询
//     * @param status
//     * @return
//     */
//    @Select("select * from orders where status = #{status}")
//    List<Orders> getByStatus(Integer status);
}
