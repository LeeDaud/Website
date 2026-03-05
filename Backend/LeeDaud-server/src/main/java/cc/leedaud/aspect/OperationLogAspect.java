package cc.leedaud.aspect;

import cc.leedaud.annotation.OperationLog;
import cc.leedaud.service.SaveLogAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 鑷畾涔夊垏闈㈢被锛岀敤浜庤褰曟搷浣滄棩蹇? */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private SaveLogAsyncService saveLogAsyncService;

    /**
     * 瀹氫箟鍒囧叆鐐?     */
    @Pointcut("@annotation(cc.leedaud.annotation.OperationLog)")
    public void operationLogPointCut() {
    }

    /**
     * 鐜粫閫氱煡
     */
    @Around("operationLogPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e; // 閲嶆柊鎶涘嚭寮傚父
        } finally {
            // 鑾峰彇娉ㄨВ
            MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 鏂规硶绛惧悕瀵硅薄
            Method method = signature.getMethod(); // 鏂规硶瀵硅薄
            OperationLog operationLog = method.getAnnotation(OperationLog.class); // 鑾峰彇鏂规硶涓婄殑娉ㄨВ瀵硅薄

            if (operationLog != null) {
                // 寮傛璁板綍鎿嶄綔鏃ュ織
                saveLogAsyncService.saveLogAsync(joinPoint, result, error, operationLog);
            }
        }
    }
}

