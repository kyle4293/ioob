package com.ioob.backend.global.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.ioob.backend..*Controller.*(..)) || execution(* com.ioob.backend..*Service.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();
        log.info("[START] {}#{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("[END] {}#{} (Execution Time: {} ms)", className, methodName, totalTime);
            return result;
        } catch (Throwable ex) {
            log.error("[ERROR] {}#{} (Message: {})", className, methodName, ex.getMessage());
            throw ex;
        }
    }

    @Around("execution(* com.ioob.backend.domain..repository.*.*(..))")
    public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        log.debug("[DB QUERY] {} executed in {} ms", methodName, executionTime);
        return result;
    }
}
