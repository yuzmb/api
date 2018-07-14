package com.mtdhb.api.aop;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/10
 */
@Component
@Aspect
public class LoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Pointcut("execution(* com.mtdhb.api.web.home.*.*(..))")
    private void controller() {
    }

    @Pointcut("execution(* com.mtdhb.api.service.impl.*.*(..))")
    private void service() {
    }

    @Around("controller()")
    public Object aroundController(ProceedingJoinPoint pjp) throws Throwable {
        return around(pjp);
    }

    @Around("service()")
    public Object aroundService(ProceedingJoinPoint pjp) throws Throwable {
        return around(pjp);
    }

    private Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 由于当前 Controller 和 Service 的类名唯一，所以暂时不打印类全名
        String invoke = pjp.getTarget().getClass().getSimpleName() + "#" + pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        Optional<String> optional = Stream.of(args).map(ojbect -> ", {}")
                .reduce((accumulator, currentValue) -> accumulator += currentValue);
        logger.info(invoke + "(" + optional.orElse("  ").substring(2) + ")", args);
        Object object = pjp.proceed();
        logger.info(invoke + " return {}", object);
        return object;
    }

}
