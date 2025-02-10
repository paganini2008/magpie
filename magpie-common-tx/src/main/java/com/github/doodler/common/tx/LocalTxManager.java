package com.github.doodler.common.tx;

import static com.github.doodler.common.tx.TxConstants.REDIS_KEY_TX;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.github.doodler.common.ApiResult;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.redis.CloudConstants;
import com.github.doodler.common.context.InstanceId;
import com.github.doodler.common.http.RestTemplateHolder;
import com.github.doodler.common.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: LocalTxManager
 * @Author: Fred Feng
 * @Date: 07/02/2025
 * @Version 1.0.0
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class LocalTxManager implements TxManager {

    private final InstanceId instanceId;
    private final TxPlatformTransactionManager txPlatformTransactionManager;
    private final TransactionTemplate transactionTemplate;
    private final StringRedisTemplate redisTemplate;
    private final DiscoveryClient discoveryClient;
    private final RestTemplateHolder restTemplateHolder;
    private final Map<String, Function<Boolean, Boolean>> manuals =
            new ConcurrentHashMap<String, Function<Boolean, Boolean>>();

    public LocalTxManager(InstanceId instanceId,
            PlatformTransactionManager platformTransactionManager,
            StringRedisTemplate redisTemplate, RedissonClient redissonClient,
            DiscoveryClient discoveryClient, RestTemplateHolder restTemplateHolder) {
        this.instanceId = instanceId;
        this.txPlatformTransactionManager =
                new TxPlatformTransactionManagerImpl(platformTransactionManager);
        this.transactionTemplate = new TransactionTemplate(txPlatformTransactionManager);
        this.redisTemplate = redisTemplate;
        this.discoveryClient = discoveryClient;
        this.restTemplateHolder = restTemplateHolder;
    }

    @Pointcut("execution(public * *(..))")
    public void signature() {}

    @Around("signature() && @annotation(tx)")
    public Object arround(ProceedingJoinPoint pjp, Tx tx) throws Throwable {
        boolean master = TxId.isMaster();
        String txId = TxId.getCurrent(master);
        if (StringUtils.isBlank(txId)) {
            throw new IllegalStateTxException("TxID is required");
        }
        manuals.put(txId, (commit) -> {
            String methodName = pjp.getSignature().getName() + (commit ? "$Commit" : "$Rollback");
            try {
                MethodUtils.invokeExactMethod(pjp.getTarget(), methodName, pjp.getArgs());
                return true;
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage());
                }
                return false;
            }
        });
        String txName = tx.name();
        try {
            return doInTx(pjp, txName, txId);
        } finally {
            if (master) {
                String key = String.format(REDIS_KEY_TX, txName, txId);
                Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                boolean commit = map.values().stream().allMatch(o -> ((Boolean) o).booleanValue());
                CompletableFuture.supplyAsync(() -> {
                    Map<String, Boolean> resultMap = new LinkedHashMap<>();
                    map.keySet().forEach(o -> {
                        String instanceId = (String) o;
                        boolean result = commitOrRollback(instanceId, txName, txId, commit);
                        resultMap.put(instanceId, result);
                    });
                    return resultMap;
                }).thenAccept(m -> {
                    if (m.values().stream().allMatch(Boolean::booleanValue) == false) {
                        m.keySet().forEach(instanceId -> {
                            forceCommitOrRollback(instanceId, txName, txId, commit);
                        });
                    }
                }).join();
                manuals.remove(txId);
                TxId.eraseCurrent();
            }
        }
    }

    private Object doInTx(ProceedingJoinPoint pjp, String txName, String txId) throws Throwable {
        AtomicReference<Throwable> thrownRef = new AtomicReference<Throwable>();
        AtomicBoolean txOk = new AtomicBoolean(false);
        Object executedResult = transactionTemplate.execute(status -> {
            Object result = null;
            try {
                result = pjp.proceed();
                txOk.set(true);
            } catch (TxException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
                status.setRollbackOnly();
            } catch (Throwable e) {
                status.setRollbackOnly();
                thrownRef.set(e);
            } finally {
                String key = String.format(REDIS_KEY_TX, txName, txId);
                redisTemplate.opsForHash().put(key, instanceId.get(), txOk.get());
            }
            return result;
        });
        if (!txOk.get() && thrownRef.get() != null) {
            throw thrownRef.get();
        }
        return executedResult;
    }

    private Optional<ApplicationInfo> findApplicationInfoByInstanceId(String instanceId) {
        return discoveryClient.getServices().stream()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
                .filter(i -> i.getMetadata() != null
                        && i.getMetadata().containsKey(CloudConstants.METADATA_APPLICATION_INFO))
                .map(i -> Base64.decodeBase64(
                        i.getMetadata().get(CloudConstants.METADATA_APPLICATION_INFO)))
                .map(s -> JacksonUtils.parseJson(s, ApplicationInfo.class))
                .filter(i -> i.getInstanceId().equals(instanceId)).findFirst();
    }

    private boolean commitOrRollback(String instanceId, String txName, String txId,
            boolean commit) {
        Optional<ApplicationInfo> opt = findApplicationInfoByInstanceId(instanceId);
        if (opt.isPresent()) {
            ApplicationInfo applicationInfo = opt.get();
            String hostUrl = applicationInfo.retriveHostUrl(false);
            String url = hostUrl + applicationInfo.getContextPath();
            url = String.format("%s/tx/%s", url, commit ? "commit" : "rollback");
            try {
                ResponseEntity<ApiResult<Boolean>> responseEntity =
                        restTemplateHolder.getRetryableRestTemplate().exchange(url, HttpMethod.PUT,
                                new HttpEntity<Object>(new TxRequest(txName, txId)),
                                new ParameterizedTypeReference<ApiResult<Boolean>>() {});
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return responseEntity.getBody().getData();
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    private boolean forceCommitOrRollback(String instanceId, String txName, String txId,
            boolean commit) {
        Optional<ApplicationInfo> opt = findApplicationInfoByInstanceId(instanceId);
        if (opt.isPresent()) {
            ApplicationInfo applicationInfo = opt.get();
            String hostUrl = applicationInfo.retriveHostUrl(false);
            String url = hostUrl + applicationInfo.getContextPath();
            url = String.format("%s/tx/%s", url, commit ? "fcommit" : "frollback");
            try {
                ResponseEntity<ApiResult<Boolean>> responseEntity =
                        restTemplateHolder.getRetryableRestTemplate().exchange(url, HttpMethod.PUT,
                                new HttpEntity<Object>(new TxRequest(txName, txId)),
                                new ParameterizedTypeReference<ApiResult<Boolean>>() {});
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return responseEntity.getBody().getData();
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    @Override
    public boolean forceCommit(String txId) {
        boolean result;
        try {
            result = txPlatformTransactionManager.commit(txId);
        } catch (Exception e) {
            result = false;
        }
        if (!result) {
            return manuals.get(txId).apply(true);
        }
        return true;
    }

    @Override
    public boolean forceRollback(String txId) {
        boolean result;
        try {
            result = txPlatformTransactionManager.rollback(txId);
        } catch (Exception e) {
            result = false;
        }
        if (!result) {
            return manuals.get(txId).apply(false);
        }
        return true;
    }

}
