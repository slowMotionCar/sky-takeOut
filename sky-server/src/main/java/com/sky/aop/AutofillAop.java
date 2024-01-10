package com.sky.aop;

import com.sky.annotation.Autofill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @Description AutofillAop
 * @Author Zhilin
 * @Date 2023-09-24
 */
@Aspect
@Slf4j
@Component
public class AutofillAop {

    /**
     * 扫描mapper包下所有类，类中带有注解@AutoFill的所有方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) &&@annotation(com.sky.annotation.Autofill)")
    public void pc() {
    }

    /**
     * 扫描对应的方法，将方法参数进行公共字段填充, 前置通知(在map前输入字段到对象中)
     *
     * @param joinPoint
     * @throws Exception
     */
    @Before("pc()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        // 1.获取原有方法上注解AutoFill对象，就得到操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Autofill autofill = methodSignature.getMethod().getAnnotation(Autofill.class);
        // 获得operationType 即逻辑符号 Enum中 INSERT or UPDATE
        OperationType operationType = autofill.value();
        log.info("[{}]公共字段填写ing", String.valueOf(operationType));

        // 2.获取原有方法参数对象
        // 通过反射获得对象 -- 通用性
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];

        // 该参数只有一个
        Class<?> argClass = arg.getClass();

        // 获得变量
        // 当前时间
        LocalDateTime ldt = LocalDateTime.now();
        // Threadlocal 获取当前用户
        long currentId = BaseContext.getCurrentId();

        // Update和Insert 均需要 Update Time and User
        Method setUpdateTime = argClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = argClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        setUpdateTime.invoke(arg, ldt);
        setUpdateUser.invoke(arg, currentId);

        // Insert 还需要 Create Time and User
        if (operationType.equals(OperationType.INSERT)) {
            Method setCreateTime = argClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = argClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            setCreateTime.invoke(arg, ldt);
            setCreateUser.invoke(arg, currentId);
        }

    }

}
