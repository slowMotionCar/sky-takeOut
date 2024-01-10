package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description Autofill
 * @Author Zhilin
 * @Date 2023-09-24
 */

// 设置注解写在方法上
@Target(ElementType.METHOD)
// 设置注解在运行时可用
@Retention(RetentionPolicy.RUNTIME)
public @interface Autofill {

    // 将枚举常量作为属性值传递给value()方法
    OperationType value();
}
