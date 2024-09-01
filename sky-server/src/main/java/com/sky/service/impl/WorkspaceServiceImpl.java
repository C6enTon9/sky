package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;



    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {

        LocalDate now = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now, LocalTime.MAX);

        //获取今日总营业额
        Double turnover = orderMapper.getSumAmount(begin, end, Orders.COMPLETED);
        //获得有效订单数
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        Integer orderCount = orderMapper.getOrderCount(map);
        map.put("status",Orders.COMPLETED);
        Integer validOrderCount = orderMapper.getOrderCount(map);
        //获得订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue()/orderCount.doubleValue();
        //获得新增用户数量
        Integer newUsers = userMapper.countUserNumber(begin, end);
        //获得平均客单价
        Integer userNumber = orderMapper.getUserNumber(begin,end,Orders.COMPLETED);
        Double unitPrice = turnover/userNumber;

        BusinessDataVO businessDataVO = BusinessDataVO.builder().newUsers(newUsers).turnover(turnover).validOrderCount(validOrderCount).orderCompletionRate(orderCompletionRate).unitPrice(unitPrice).build();

        return businessDataVO;
    }


    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {

        Integer sold = setmealMapper.getByStatus(1);
        Integer discontinued = setmealMapper.getByStatus(0);

        SetmealOverViewVO setmealOverViewVO = new SetmealOverViewVO();
        setmealOverViewVO.setSold(sold);
        setmealOverViewVO.setDiscontinued(discontinued);
        return setmealOverViewVO;
    }


    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {

        Integer sold = dishMapper.getByStatus(1);
        Integer discontinued = dishMapper.getByStatus(0);

        DishOverViewVO dishOverViewVO = new DishOverViewVO();
        dishOverViewVO.setSold(sold);
        dishOverViewVO.setDiscontinued(discontinued);
        return dishOverViewVO;
    }


    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {

        LocalDate now = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now, LocalTime.MAX);

        Integer waitingOrders = orderMapper.countBystatus(begin,end,Orders.TO_BE_CONFIRMED);
        Integer deliveredOrders = orderMapper.countBystatus(begin,end,Orders.CONFIRMED);
        Integer completedOrders = orderMapper.countBystatus(begin,end,Orders.COMPLETED);
        Integer cancelledOrders = orderMapper.countBystatus(begin,end,Orders.CANCELLED);
        Integer allOrders = orderMapper.countAllOrders(begin,end);

        return OrderOverViewVO
                .builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }
}
