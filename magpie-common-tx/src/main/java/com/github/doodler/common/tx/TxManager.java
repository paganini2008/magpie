package com.github.doodler.common.tx;

/**
 * 
 * @Description: TxManager
 * @Author: Fred Feng
 * @Date: 09/02/2025
 * @Version 1.0.0
 */
public interface TxManager {

    boolean forceCommit(String txId);

    boolean forceRollback(String txId);

}
