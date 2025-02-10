package com.github.doodler.common.tx;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;

/**
 * 
 * @Description: TxPlatformTransactionManager
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
public interface TxPlatformTransactionManager extends PlatformTransactionManager {

    boolean commit(String txId) throws TransactionException;

    boolean rollback(String txId) throws TransactionException;

}
