package com.github.doodler.common.tx;

/**
 * 
 * @Description: TxInterruptedException
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
public class TxInterruptedException extends TxException {

    private static final long serialVersionUID = -6522674668105034242L;

    public TxInterruptedException(Throwable e) {
        super(e);
    }

    public TxInterruptedException(String msg, Throwable e) {
        super(msg, e);
    }

}
