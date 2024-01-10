package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
// testing
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "职员控制器")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登陆接口")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("登出接口")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 添加职工
     * @param employeeDTO
     * @return
     */
    @PostMapping("")
    @ApiOperation("添加职工接口")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        System.out.println(employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 职工分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("职工分页")
    public Result displayEmployee(int page, int pageSize, String name) {
        EmployeePageQueryDTO employeePageQueryDTO = EmployeePageQueryDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .name(name)
                .build();
        System.out.println(employeePageQueryDTO);
        PageResult pageResult = employeeService.displayEmployee(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 转黄账号状态
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation("修改员工状态")
    public Result changeStatus(@PathVariable String status, @RequestParam String id) {
        // 封装id和状态
        Employee employee = new Employee();
        int intStatus = Integer.parseInt(status);
        int intId = Integer.parseInt(id);
        employee.setStatus(intStatus);
        employee.setId((long) intId);
        // System.out.println(employee);
        employeeService.changeStatus(employee);

        return Result.success();
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查找员工用ID")
    public Result<Employee> getEmployeeById(@PathVariable String id) {

        Employee employeeReturn = employeeService.getEmployeeById(id);

        return Result.success(employeeReturn);
    }


    /**
     *编辑员工
     * @param employeeDTO
     * @return
     */
    @PutMapping("")
    @ApiOperation("编辑员工")
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO) {

        employeeService.updateEmployee(employeeDTO);

        return Result.success();
    }
}
