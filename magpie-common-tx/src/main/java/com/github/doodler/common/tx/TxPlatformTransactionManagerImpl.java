package com.github.doodler.common.tx;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import com.github.doodler.common.utils.SingleObservable;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: TxPlatformTransactionManagerImpl
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class TxPlatformTransactionManagerImpl implements TxPlatformTransactionManager {

    private final PlatformTransactionManager platformTransactionManager;
    private final SingleObservable observable = new SingleObservable(false);

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition)
            throws TransactionException {
        return platformTransactionManager.getTransaction(definition);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        final String txId = TxId.getCurrent(false);
        if (StringUtils.isNotBlank(txId)) {
            observable.addObserver("commit:" + txId, (ob, arg) -> {
                platformTransactionManager.commit(status);
            });
        } else {
            platformTransactionManager.commit(status);
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        final String txId = TxId.getCurrent(false);
        if (StringUtils.isNotBlank(txId)) {
            observable.addObserver("rollback:" + txId, (ob, arg) -> {
                platformTransactionManager.rollback(status);
            });
        } else {
            platformTransactionManager.rollback(status);
        }
    }

    @Override
    public boolean commit(String txId) throws TransactionException {
        String channel = "commit:" + txId;
        if (!observable.hasChannel(channel)) {
            return false;
        }
        observable.notifyObservers(channel, txId);
        return true;
    }

    @Override
    public boolean rollback(String txId) throws TransactionException {
        String channel = "rollback:" + txId;
        if (observable.hasChannel(channel)) {
            observable.notifyObservers(channel, txId);
        }
        return true;
    }



}
