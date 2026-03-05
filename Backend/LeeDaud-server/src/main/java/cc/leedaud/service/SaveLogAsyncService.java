package cc.leedaud.service;

import cc.leedaud.annotation.OperationLog;
import org.aspectj.lang.JoinPoint;

public interface SaveLogAsyncService {

    /**
     * 寮傛淇濆瓨鏃ュ織
     * @param joinPoint
     * @param result
     * @param error
     * @param operationLog
     */
    void saveLogAsync(JoinPoint joinPoint, Object result, Throwable error,
                      OperationLog operationLog);
}

