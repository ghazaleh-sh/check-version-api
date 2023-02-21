package ir.sadad.co.checkversionapi.commons.aspects;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class VersionLoggingAspect {

    @Before("execution(* ir.sadad.co.checkversionapi.services.VersionServiceImpl.*(..))")
    public void beforeAllServiceMethods(JoinPoint joinPoint) {
        log.info("********** started executing: " + joinPoint.getSignature().getName() +
                " with method param: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* ir.sadad.co.checkversionapi.services.VersionServiceImpl.*(..))", returning = "result")
    public void afterAllServiceMethods(JoinPoint joinPoint, Object result) {
        log.info("********** completed executing: " + joinPoint.getSignature().getName() +
                " with return value: " + result);
    }

    @AfterThrowing(pointcut = "execution(* ir.sadad.co.checkversionapi.services.VersionServiceImpl.*(..))", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getMessage() != null ? e.getMessage() : "NULL");
    }
}
