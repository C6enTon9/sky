package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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


    /**
     * 根据时间区间已经订单状态获得营业额
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    Double getSumAmount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 根据时间以及订单状态等条件查询订单数
     * @param map
     * @return
     */
    Integer getOrderCount(Map map);


    /**
     * 查询top10销量的菜品
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime);


    /**
     * 查询下单的用户数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    Integer getUserNumber(LocalDateTime begin, LocalDateTime end, Integer status);


    /**
     * 根据状态查询订单数
     * @param begin
     * @param end
     * @param status
     * @return
     */
    @Select("select count(id) from orders where order_time between #{begin} and #{end} and status =#{status}")
    Integer countBystatus(LocalDateTime begin, LocalDateTime end, Integer status);


    /**
     * 查询全部订单数量
     * @param begin
     * @param end
     * @return
     */
    @Select("select count(id) from orders where order_time between #{begin} and #{end}")
    Integer countAllOrders(LocalDateTime begin, LocalDateTime end);


//    /**
//     * 根据订单状态查询
//     * @param status
//     * @return
//     */
//    @Select("select * from orders where status = #{status}")
//    List<Orders> getByStatus(Integer status);
}
