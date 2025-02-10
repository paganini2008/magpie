package com.github.doodler.common.tx;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.doodler.common.context.HttpRequestContextHolder;

/**
 * 
 * @Description: TxId
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
public final class TxId {

    private TxId() {}

    private static final ThreadLocal<String> txIdCache = new TransmittableThreadLocal<String>();

    public static boolean isMaster() {
        String txId = HttpRequestContextHolder.getHeader("TX-ID");
        if (StringUtils.isNotBlank(txId)) {
            return false;
        }
        return StringUtils.isBlank(txIdCache.get());
    }

    public static String getCurrent(boolean createIfNew) {
        String txId = HttpRequestContextHolder.getHeader("TX-ID");
        if (StringUtils.isNotBlank(txId)) {
            txIdCache.set(txId);
        } else if (createIfNew && StringUtils.isBlank(txIdCache.get())) {
            String newTxId = "Tx-" + UUID.randomUUID().toString();
            txIdCache.set(newTxId);
            HttpRequestContextHolder.setHeader("TX-ID", newTxId);
        }
        return txIdCache.get();
    }

    public static void eraseCurrent() {
        txIdCache.remove();
    }

}
