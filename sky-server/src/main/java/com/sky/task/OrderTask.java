package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {


    @Autowired
    private OrderMapper ordermapper;
    /**
     * 处理超市订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){

        log.info("处理超时订单；{}", LocalDateTime.now());

        List<Orders> list = ordermapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,LocalDateTime.now().plusMinutes(-15));

        if(list != null && list.size() > 0){
            for (Orders orders : list) {

                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());

                ordermapper.update(orders);
            }
        }
    }


    /**
     * 处理配送超时订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){

        log.info("处理配送超时订单:{}", LocalDateTime.now());

        List<Orders> ordersList = ordermapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.MAX.plusMinutes(-60));
        if (ordersList != null && ordersList.size() > 0) {
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                ordermapper.update(orders);
            });
        }
    }
}
