package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     *
     * @param employeeDTO
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    PageResult displayEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     *
     * @param employee
     */
    void changeStatus(Employee employee);

    /**
     *
     * @param id
     * @return
     */
    Employee getEmployeeById(String id);

    /**
     *
     * @param employeeDTO
     */
    void updateEmployee(EmployeeDTO employeeDTO);
}
