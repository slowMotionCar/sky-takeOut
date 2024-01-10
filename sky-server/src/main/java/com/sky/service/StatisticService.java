package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @Description StatisticService
 * @Author Zhilin
 * @Date 2023-10-08
 */
public interface StatisticService {
    /**
     * 营业额统计接口
     * @return
     */
    TurnoverReportVO turnoverStatistic(LocalDate begin,LocalDate end);

    UserReportVO customersStatstic(LocalDate begin, LocalDate end);

    OrderReportVO statisticOrders(LocalDate begin, LocalDate end);

    SalesTop10ReportVO statisticTop10(LocalDate begin, LocalDate end);

    void excelByEasyExcel(HttpServletResponse response);

    // void excelByApachePOI(HttpServletResponse response);
}
