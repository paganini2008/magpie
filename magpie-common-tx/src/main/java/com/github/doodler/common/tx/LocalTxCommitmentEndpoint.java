package com.github.doodler.common.tx;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.doodler.common.ApiResult;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: LocalTxCommitmentEndpoint
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
@RequestMapping("/tx")
@RestController
@RequiredArgsConstructor
public class LocalTxCommitmentEndpoint {

    private final TxPlatformTransactionManager txPlatformTransactionManager;
    private final TxManager txManager;

    @PutMapping("/commit")
    public ApiResult<Boolean> commit(@RequestBody TxRequest txRequest) {
        return ApiResult.ok(txPlatformTransactionManager.commit(txRequest.getTxId()));
    }

    @PutMapping("/rollback")
    public ApiResult<Boolean> rollback(@RequestBody TxRequest txRequest) {
        return ApiResult.ok(txPlatformTransactionManager.rollback(txRequest.getTxId()));
    }

    @PutMapping("/fcommit")
    public ApiResult<Boolean> fcommit(@RequestBody TxRequest txRequest) {
        return ApiResult.ok(txManager.forceCommit(txRequest.getTxId()));
    }

    @PutMapping("/frollback")
    public ApiResult<Boolean> frollback(@RequestBody TxRequest txRequest) {
        return ApiResult.ok(txManager.forceRollback(txRequest.getTxId()));
    }
}
