package com.sky.service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    /**
     * 统计营业额
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);


    /**
     * 用户数量统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);


    /**
     * 查询销量Top10菜品
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
}
