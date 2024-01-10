package com.sky.mapper;

import com.sky.annotation.Autofill;
import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     *
     * @param employee
     */
    @Autofill(value = OperationType.INSERT)
    void addEmployee(Employee employee);

    List displayEmployee(String name);

    @Autofill(value = OperationType.UPDATE)
    void updateEmployee(Employee employee);

    Employee getEployeeById(String id);
}
