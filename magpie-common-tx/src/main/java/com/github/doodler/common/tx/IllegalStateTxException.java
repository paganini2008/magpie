package com.github.doodler.common.tx;

/**
 * 
 * @Description: IllegalStateTxException
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
public class IllegalStateTxException extends TxException {

    private static final long serialVersionUID = 2028512360873977870L;

    public IllegalStateTxException() {
        super();
    }

    public IllegalStateTxException(String msg) {
        super(msg);
    }

    public IllegalStateTxException(Throwable e) {
        super(e);
    }

    public IllegalStateTxException(String msg, Throwable e) {
        super(msg, e);
    }

}
