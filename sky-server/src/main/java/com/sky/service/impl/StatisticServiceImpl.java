package com.sky.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.StatisticService;
import com.sky.vo.*;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description StatisticServiceImpl
 * @Author Zhilin
 * @Date 2023-10-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    /**
     * 营业额统计接口
     *
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistic(LocalDate begin, LocalDate end) {
        log.info("hello");

        // 包含储存时间的list和储存turnover的list 转化后的string
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();

        // 把时间装配到list中
        List<LocalDate> duration = new ArrayList<>();
        // 这里+1因为前端配置了-1day去查询
        while (!begin.equals(end.plusDays(1))) {
            duration.add(begin);
            begin = begin.plusDays(1);
            System.out.println("循环内" + duration);
        }

        List<Double> amount = new ArrayList<>();
        // 根据传入时间段查询订单额
        for (LocalDate localDate : duration) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            // 封装一个实参
            Map map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            // 查询订单总额
            Double amountByDay = 0.0;

            Double amountByDayTest = orderMapper.sumByMap(map);
            if (amountByDayTest != null) {
                amountByDay = amountByDayTest;
            }
            amount.add(amountByDay);
        }

        // 封装

        turnoverReportVO.setDateList(StringUtils.join(duration, ","));
        turnoverReportVO.setTurnoverList(StringUtils.join(amount, ","));

        return turnoverReportVO;
    }

    /**
     * 统计用户数据接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO customersStatstic(LocalDate begin, LocalDate end) {

        UserReportVO userReportVO = new UserReportVO();

        // List装配从begin到end
        List<LocalDate> date = new ArrayList<>();
        while (!begin.equals(end.plusDays(1))) {
            date.add(begin);
            begin = begin.plusDays(1);
        }
        LocalDateTime customersBeforeTime = LocalDateTime.of(date.get(0), LocalTime.MIN);
        // 查找第一日前所有用户
        Map map = new HashMap<>();
        map.put("endTime", customersBeforeTime);
        Integer customersCount = orderMapper.customersStatistic(map);
        System.out.println(customersCount);
        // 新增总用户数和单独用户数量List
        List<Integer> customersInTotal = new ArrayList<Integer>();
        List<Integer> customersPerDay = new ArrayList<Integer>();
        System.out.println("到这里了");
        // 遍历找到每一个日期的总用户数和单独用户数
        for (LocalDate localDate : date) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map mapLoop = new HashMap<>();
            mapLoop.put("beginTime", beginTime);
            mapLoop.put("endTime", endTime);
            // 添加单独用户数
            Integer customers = orderMapper.customersStatistic(mapLoop);
            customersPerDay.add(customers);
            // 添加总用户数
            customersCount += customers;
            customersInTotal.add(customersCount);
        }

        userReportVO.setDateList(StringUtils.join(date, ","));
        userReportVO.setNewUserList(StringUtils.join(customersPerDay, ","));
        userReportVO.setTotalUserList(StringUtils.join(customersInTotal, ","));
        return userReportVO;
    }

    /**
     * 商家订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO statisticOrders(LocalDate begin, LocalDate end) {

        // 把日期封装List
        List<LocalDate> date = new ArrayList<>();
        while (!begin.equals(end.plusDays(1))) {
            date.add(begin);
            begin = begin.plusDays(1);
        }
        System.out.println(date);

        // 创建参数收集全部订单
        Integer countTotal = 0;
        // 创建参数收集有效订单
        Integer countFinished = 0;
        // 创建List收集全部订单
        List orderTotalByDay = new ArrayList<>();
        // 创建List收集有效订单
        List orderFinishedByDay = new ArrayList<>();
        // 通过日期遍历
        for (LocalDate localDate : date) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            // 总数
            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer numTotal = orderMapper.countByMap(map);
            countTotal += numTotal;
            orderTotalByDay.add(numTotal);
            // 有效订单
            map.put("status", Orders.COMPLETED);
            Integer numFinished = orderMapper.countByMap(map);
            countFinished += numFinished;
            orderFinishedByDay.add(numFinished);
        }
        // 订单完成率

        Double rateFinished = 0.0;
        if (countTotal != 0) {
            rateFinished = countFinished * 1.0 / countTotal;
        }
        return OrderReportVO.builder()
                .orderCountList(StringUtils.join(orderTotalByDay, ","))
                .dateList(StringUtils.join(date, ","))
                .validOrderCountList(StringUtils.join(orderFinishedByDay, ","))
                .validOrderCount(countFinished)
                .totalOrderCount(countTotal)
                .orderCompletionRate(rateFinished)
                .build();
    }

    /**
     * 销量排序前十接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO statisticTop10(LocalDate begin, LocalDate end) {

        // 多表查询

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map map = new HashMap<>();
        map.put("status", Orders.COMPLETED);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        // 用GoodSalesDto封装
        List<GoodsSalesDTO> Top10 = orderMapper.findTop10(map);
        // 异常
        if (Collections.isEmpty(Top10)) {
            return null;
        }
        // 映射
        List<String> collectName = Top10.stream().map((item) -> {
            String name = item.getName();
            return name;
        }).collect(Collectors.toList());

        List<Integer> collectNumber = Top10.stream().map((item) -> {
            Integer number = item.getNumber();
            return number;
        }).collect(Collectors.toList());


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(collectName, ","))
                .numberList(StringUtils.join(collectNumber, ","))
                .build();
    }

    /**
     * 通过EasyExcel拿取Excel
     */
    @Override
    public void excelByEasyExcel(HttpServletResponse response) {

        String templateFileName = this.getClass().getClassLoader().getResource("template/data.xlsx").getPath();
        try(ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templateFileName).build()){
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            //获取30天概览数据
            LocalDate begin = LocalDate.now().minusDays(30);
            LocalDate end = LocalDate.now().minusDays(1);

            LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);

            //写入
            BusinessDataVO businessDataVO = getBusinessDataVO(map);
            excelWriter.fill(businessDataVO,writeSheet);

            //获取每天的明细数据
            List<BusinessDataVO> list = new ArrayList<>();

            while (!begin.equals(end)){
                LocalDateTime beginTimeEach = LocalDateTime.of(begin,LocalTime.MIN);
                LocalDateTime endTimeEach = LocalDateTime.of(begin,LocalTime.MAX);

                Map map1 = new HashMap<>();
                map1.put("beginTime",beginTimeEach);
                map1.put("endTime",endTimeEach);
                BusinessDataVO businessDataVOEach = getBusinessDataVO(map1);
                businessDataVOEach.setDate(begin.toString());

                list.add(businessDataVOEach);
                begin = begin.plusDays(1);
            }
            //写入

            excelWriter.fill(list,writeSheet);

        }catch (Exception exception){
            exception.printStackTrace();
        }

    }


    // /**
    //  * 导出近30天的运营数据报表
    //  *
    //  * @param response
    //  */
      /*
    @Override
    public void excelByApachePOI(HttpServletResponse response) {

        // 读取xlsx模板
        // 根据类获取流(非磁盘,所以不能用路径 否则空指针)
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        // 获取30天数据
        LocalDateTime begin = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);

        // 统计订单总数
        Map map = new HashMap<>();
        map.put("beginTime", begin);
        map.put("endTime", end);

        BusinessDataVO businessDataVO = getBusinessDataVO(map);

        try {
            if (inputStream == null) {
                throw new BaseException("is is null");
            }

            // 写入excel概览数据列表
            XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = sheets.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("查询: " + begin.toLocalDate() + "到 " + end.toLocalDate() + "的商户运营数据");
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // 写入明细数据

            LocalDate start = begin.toLocalDate();
            for (int i = 0; i < 30; i++) {
                System.out.println("startBegin"+start);
                LocalDateTime beginTime = LocalDateTime.of(start, LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(start, LocalTime.MAX);
                Map mapTemp = new HashMap<>();
                mapTemp.put("beginTime", beginTime);
                mapTemp.put("endTime", endTime);

                BusinessDataVO businessDataTemp = getBusinessDataVO(mapTemp);
                System.out.println("kankan"+businessDataTemp.getTurnover());
                // 写入
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(start.toString());
                row.getCell(2).setCellValue(businessDataTemp.getTurnover());
                row.getCell(3).setCellValue(businessDataTemp.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataTemp.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataTemp.getUnitPrice());
                row.getCell(6).setCellValue(businessDataTemp.getNewUsers());

                start = start.plusDays(1);
                System.out.println("startEnd"+start);
            }

            // 写出报表
            ServletOutputStream outputStream = response.getOutputStream();
            sheets.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    */
    /**
     * 根据开始和结束时间得到运营数据
     *
     * @param map
     * @return
     */
    private BusinessDataVO getBusinessDataVO(Map map) {
        Integer totalOrder = orderMapper.countByMap(map);
        // 统计新增客户
        Integer customerCount = orderMapper.customersStatistic(map);
        // 统计有效订单总数
        map.put("status", Orders.COMPLETED);
        Integer finishedOrder = orderMapper.countByMap(map);
        // 统计营业额
        Double totalAmount = 0.0;
        Double totalAmountTest = orderMapper.sumByMap(map);
        if(totalAmountTest!=null){
            totalAmount = totalAmountTest;
        }
        // 计算订单完成率
        Double orderFinishedRate = 0.0;
        if (totalOrder != 0) {
            orderFinishedRate = finishedOrder * 1.0 / totalOrder;
        }

        // 计算平均客单率
        Double amountPerOrder = 0.0;
        if (finishedOrder != 0) {
            amountPerOrder = totalAmount * 1.0 / finishedOrder;
        }
        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .newUsers(customerCount)
                .orderCompletionRate(orderFinishedRate)
                .unitPrice(amountPerOrder)
                .validOrderCount(finishedOrder)
                .turnover(totalAmount)
                .build();
        return businessDataVO;
    }
}
