package cc.leedaud.exception;

import cc.leedaud.constant.MessageConstant;

public class PasswordErrorException extends BaseException{
    public PasswordErrorException() {
    }
    public PasswordErrorException(String msg) {
        super(msg);
    }
}

