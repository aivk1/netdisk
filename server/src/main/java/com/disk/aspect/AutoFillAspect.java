package com.disk.aspect;

import com.disk.annotation.AutoFill;
import com.disk.constant.AutoFillConstant;
import com.disk.context.BaseContext;
import com.disk.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.disk.mapper.*.*(..)) && @annotation(com.disk.annotation.AutoFill)")
    public void autoFillPointCut(){}
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始公共字段的填充");


        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AutoFill autofill = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autofill.value();

        //获取到当前被拦截方法的参数-实体对象
        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();


        Object[] args = joinPoint.getArgs();
        if(args!=null && args.length==0){
            return ;
        }
        Object entity = args[0];
        if (operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUser.invoke(entity, id);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, id);
                setCreateTime.invoke(entity, now);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            try{
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUser.invoke(entity, id);
                setUpdateTime.invoke(entity, now);
            } catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
