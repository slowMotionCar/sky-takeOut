package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Value("${md5.code.keyword}")
    private String secretKey;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        employeeLoginDTO.setPassword(DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes()));
        String password = employeeLoginDTO.getPassword();
        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        //  后期需要进行md5加密，然后再进行比对 (已完成)
        if (!password.equals(employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            // 账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        // 复制employDTO到employ
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setCreateUser(BaseContext.getCurrentId());
        // employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setStatus(StatusConstant.ENABLE);
        // password赋值
        // oktodo 加盐密码
        if (employee.getPassword() == null) {
            String md5key = DigestUtils.md5DigestAsHex((PasswordConstant.DEFAULT_PASSWORD + secretKey).getBytes());
            log.info("加盐后密码是{}",md5key);
            employee.setPassword(md5key);
        }
        System.out.println(employee);
        employeeMapper.addEmployee(employee);
    }

    /**
     * 分页
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult displayEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("用了displayEmployee");
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        String name = employeePageQueryDTO.getName();
        List<Employee> list = employeeMapper.displayEmployee(name);
        // System.out.println(list);
        Page pages = (Page) list;
        PageResult pageResult = new PageResult(pages.getTotal(), pages.getResult());

        return pageResult;
    }

    /**
     * @param employee
     */
    @Override
    public void changeStatus(Employee employee) {
        // employee.setUpdateUser(BaseContext.getCurrentId());
        // employee.setUpdateTime(LocalDateTime.now());
        employee.setStatus((int) employee.getStatus());
        employeeMapper.updateEmployee(employee);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public Employee getEmployeeById(String id) {
        log.info("用了getemploybyid");
        Employee employeeReturn = employeeMapper.getEployeeById(id);

        return employeeReturn;
    }

    /**
     *
     * @param employeeDTO
     */
    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateEmployee(employee);
    }

}
