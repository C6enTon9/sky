package com.sky.controller.admin;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@Slf4j
@Api(tags = "数据统计相关接口")
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;


    @GetMapping("/turnoverStatistics")
    @ApiOperation("统计营业额")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("统计营业额：{}，{}", begin, end);

        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);

        return Result.success(turnoverReportVO);
    }


    /**
     * 用户数量统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("统计用户数量：{}，{}", begin, end);

        UserReportVO userReportVO = reportService.userStatistics(begin,end);

        return Result.success(userReportVO);
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计：{}，{}", begin, end);

        OrderReportVO orderReportVO = reportService.ordersStatistics(begin,end);
        return Result.success(orderReportVO);
    }


    /**
     * 查询top10菜品
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("查询top10菜品")
    @GetMapping("top10")
    public Result<SalesTop10ReportVO> getTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询TOP10菜品：{}，{}", begin, end);

        SalesTop10ReportVO salesTop10ReportVO = reportService.getTop10(begin,end);

        return Result.success(salesTop10ReportVO);
    }
}
