package com.seungwonlee.urlshortener.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(toLog)")
    public Object logAround(ProceedingJoinPoint joinPoint, ToLog toLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        log.info("Method '{}' took {} milliseconds to execute.", joinPoint.getSignature().toShortString(), elapsedTime);
        return result;
    }

}
