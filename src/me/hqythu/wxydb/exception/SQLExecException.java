package me.hqythu.wxydb.exception;

/**
 * Created by apple on 15/10/26.
 */
public class SQLExecException extends Exception {
    public SQLExecException() {
        super();
    }
    public SQLExecException(String msg) {
        super(msg);
    }
}
