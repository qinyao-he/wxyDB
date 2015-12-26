package me.hqythu.wxydb.exception;

/**
 * Created by apple on 15/12/26.
 */
public class SQLQueryException extends Exception {
    public SQLQueryException() {
        super();
    }

    public SQLQueryException(String msg) {
        super(msg);
    }
}
