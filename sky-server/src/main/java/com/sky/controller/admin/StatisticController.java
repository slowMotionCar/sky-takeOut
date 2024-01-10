package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.StatisticService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @Description StatisticController
 * @Author Zhilin
 * @Date 2023-10-08
 */

@RestController
@Api(tags = "商家端数据统计相关接口")
@Slf4j
@RequestMapping("admin/report")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;


    /**
     * 营业额统计接口
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("营业额统计接口")
    @GetMapping("/turnoverStatistics")
    public Result turnoverStatistic(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("计算{}和{}的营业额",begin,end);

        TurnoverReportVO turnoverReportVO = statisticService.turnoverStatistic(begin,end);

        return Result.success(turnoverReportVO);
    }


    /**
     * 统计用户数据接口
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("统计用户数据接口")
    @GetMapping("/userStatistics")
    public Result customersStatistic(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("统计用户数据从{}到{}",begin,end);

        UserReportVO userReportVO = statisticService.customersStatstic(begin,end);

        return Result.success(userReportVO);
    }


    /**
     * 商家订单统计
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("商家订单统计")
    @GetMapping("/ordersStatistics")
    public Result statisticOrders(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("商家订单统计for{}and{}",begin,end);
        OrderReportVO orderReportVO = statisticService.statisticOrders(begin,end);
        return Result.success(orderReportVO);
    }


    /**
     * 销量排序前十接口
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("销量排序前十接口")
    @GetMapping("/top10")
    public Result statisticTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        log.info("销量排序前十接口");

        SalesTop10ReportVO salesTop10ReportVO = statisticService.statisticTop10(begin,end);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 通过ApachePOI拿取Excel
     */
    // @ApiOperation("通过ApachePOI拿取Excel")
    // @GetMapping("/export")
    // public void excelByApachePOI(HttpServletResponse response){
    //     log.info("通过ApachePOI拿取Excel");
    //     statisticService.excelByApachePOI(response);
    // }

    /**
     * 通过EasyExcel拿取Excel
     */
    @ApiOperation("通过EasyExcel拿取Excel")
    @GetMapping("/export")
    public void excelByEasyExcel(HttpServletResponse response){
        statisticService.excelByEasyExcel(response);
    }
}
