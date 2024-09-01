package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        //构建时间列表字符串
        List<LocalDate> localDateList = new ArrayList<>();
        while (!begin.equals(end)) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
        }
        localDateList.add(end);

        //构建营业额列表字符串
        List<Double> amountList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Double amount = orderMapper.getSumAmount(beginTime, endTime, Orders.COMPLETED);

            amount = (amount == null ? 0.0 : amount);

            amountList.add(amount);
        }

        return TurnoverReportVO.builder().dateList(StringUtils.join(localDateList, ",")).turnoverList(StringUtils.join(amountList, ",")).build();
    }


    /**
     * 用户数量统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        LocalDateTime startTime = LocalDateTime.of(begin, LocalTime.MIN);

        //日期字符串的生成
        List<LocalDate> localDateList = new ArrayList<>();
        while (!begin.equals(end)) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
        }
        localDateList.add(end);
        String localDateString = StringUtils.join(localDateList, ",");

        //用户总量字符串的生成
        ArrayList<Integer> countAllList = new ArrayList<>();
        //每日用户数量统计
        ArrayList<Integer> countDayList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Integer countAll = userMapper.countUserNumber(startTime,endTime);
            countAllList.add(countAll);

            Integer countDay = userMapper.countUserNumber(beginTime,endTime);
            countDayList.add(countDay);
        }
        String countAllString = StringUtils.join(countAllList, ",");
        String countDayString = StringUtils.join(countDayList, ",");

        UserReportVO userReportVO = UserReportVO.builder().dateList(localDateString).totalUserList(countAllString).newUserList(countDayString).build();

        return userReportVO;
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime startTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime stopTime = LocalDateTime.of(end, LocalTime.MAX);

        //日期字符串的生成
        List<LocalDate> localDateList = new ArrayList<>();
        while (!begin.equals(end)) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
        }
        localDateList.add(end);
        String localDateString = StringUtils.join(localDateList, ",");

        //计算每日的订单总数和每日的有效订单数
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer orderCount = orderMapper.getOrderCount(map);
            if (orderCount == null) {
                orderCount = 0;
            }
            orderCountList.add(orderCount);

            map.put("status",Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderCount(map);
            if (validOrderCount == null) {
                validOrderCount = 0;
            }
            validOrderCountList.add(validOrderCount);
        }
        String orderCountListString = StringUtils.join(orderCountList, ",");
        String validOrderCountListString = StringUtils.join(validOrderCountList, ",");

        Map map = new HashMap<>();
        map.put("begin",startTime);
        map.put("end",stopTime);
        Integer totalOrderCount = orderMapper.getOrderCount(map);

        map.put("status",Orders.COMPLETED);
        Integer validOrderCount = orderMapper.getOrderCount(map);


        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount.doubleValue();
        }

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .dateList(localDateString)
                .orderCountList(orderCountListString)
                .validOrderCountList(validOrderCountListString)
                .build();

        return orderReportVO;
    }


    /**
     * 查询销量Top10的菜品
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> list = orderMapper.getTop10(beginTime,endTime);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        String nameString = StringUtils.join(nameList, ",");
        String numberString = StringUtils.join(numberList, ",");

        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder().nameList(nameString).numberList(numberString).build();
        return salesTop10ReportVO;
    }
}
