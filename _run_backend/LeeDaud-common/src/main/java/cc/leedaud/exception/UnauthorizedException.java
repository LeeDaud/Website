package cc.leedaud.exception;

import cc.leedaud.constant.MessageConstant;

public class UnauthorizedException extends TokenException{
    public UnauthorizedException() {
    }
    public UnauthorizedException(String msg) {
        super(msg);
    }
}

