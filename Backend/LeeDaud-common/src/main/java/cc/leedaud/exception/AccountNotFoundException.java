package cc.leedaud.exception;

import cc.leedaud.exception.BaseException;

/**
 * 璐﹀彿涓嶅瓨鍦ㄥ紓甯? */
public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}

