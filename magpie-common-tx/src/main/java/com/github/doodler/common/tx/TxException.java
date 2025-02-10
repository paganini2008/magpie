package com.github.doodler.common.tx;

/**
 * 
 * @Description: TxException
 * @Author: Fred Feng
 * @Date: 08/02/2025
 * @Version 1.0.0
 */
public class TxException extends Exception {

    private static final long serialVersionUID = -6877367799905179452L;

    public TxException() {
        super();
    }

    public TxException(String msg) {
        super(msg);
    }

    public TxException(Throwable e) {
        super(e);
    }

    public TxException(String msg, Throwable e) {
        super(msg, e);
    }

}
