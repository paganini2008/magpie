package com.github.doodler.common.redis;

import java.lang.reflect.Parameter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: SharedLockAspect
 * @Author: Fred Feng
 * @Date: 17/01/2025
 * @Version 1.0.0
 */
@Aspect
@RequiredArgsConstructor
public class SharedLockAspect implements EnvironmentAware {

    private final RedissonClient redissonClient;

    @Setter
    private Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;

    @Pointcut("execution(public * *(..))")
    public void signature() {}

    @Around("signature() && @annotation(sharedLock)")
    public Object doAround(ProceedingJoinPoint jp, SharedLock sharedLock) throws Throwable {
        final String key = StringUtils.isNotBlank(sharedLock.key())
                ? calculateKeyExpression(sharedLock.key(), jp)
                : "shared-lock";
        final String lockName = String.format("%s:%s",
                StringUtils.isNotBlank(sharedLock.prefix()) ? resolveKeyPrefix(sharedLock.prefix())
                        : applicationName,
                key);

        RLock lock = sharedLock.fair() ? redissonClient.getFairLock(lockName)
                : redissonClient.getLock(lockName);
        try {
            if (sharedLock.duration() > 0) {
                lock.lock(sharedLock.duration(), sharedLock.timeUnit());
            } else {
                lock.lock();
            }
            return jp.proceed();
        } finally {
            lock.unlock();
        }
    }

    private String resolveKeyPrefix(String prefix) {
        if (prefix.startsWith("${") && prefix.endsWith("}")) {
            return ((ConfigurableEnvironment) environment).resolvePlaceholders(prefix);
        }
        return prefix;
    }

    private String calculateKeyExpression(String key, ProceedingJoinPoint jp) {
        Object[] args = jp.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (ArrayUtils.isNotEmpty(args)) {
            Parameter[] parameters =
                    ((MethodSignature) jp.getSignature()).getMethod().getParameters();
            for (int i = 0; i < args.length; i++) {
                context.setVariable("p" + i, args[i]);
                context.setVariable("a" + i, args[i]);
                context.setVariable(parameters[i].getName(), args[i]);
            }
        }
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(key);
        return expression.getValue(context, String.class);
    }

}
