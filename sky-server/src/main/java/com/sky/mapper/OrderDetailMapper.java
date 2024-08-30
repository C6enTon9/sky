package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细
     * @param orderDetailList
     */
    void insertBatch(ArrayList<OrderDetail> orderDetailList);


    /**
     * 根据订单id查询订单明细
     * @param id
     * @return
     */
    List<OrderDetail> getByOrderNumber(Long id);
}
