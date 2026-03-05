package cc.leedaud.aspect;

import cc.leedaud.annotation.AutoFill;
import cc.leedaud.constant.AutoFillConstant;
import cc.leedaud.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 鑷畾涔夊垏闈㈢被锛岀敤浜庤嚜鍔ㄥ～鍏呭姛鑳藉瓧娈? */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 鍒囧叆鐐?     */
    @Pointcut("execution(* cc.leedaud.mapper.*.*(..)) && @annotation(cc.leedaud.annotation.AutoFill)")
    public void autoFillPointcut() {
    }

    /**
     * 鍓嶇疆閫氱煡
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("AOP濉厖鍏叡瀛楁");
        // 鑾峰彇褰撳墠鏂规硶鐨勬搷浣滅被鍨?        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();// 鏂规硶绛惧悕瀵硅薄
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);// 鑾峰彇鏂规硶涓婄殑娉ㄨВ瀵硅薄
        OperationType operationType = autoFill.value();// 鑾峰彇鏁版嵁搴撴搷浣滅被鍨?
        // 鑾峰彇鏂规硶鍙傛暟
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        // 鍑嗗璧嬪€肩殑鏁版嵁
        LocalDateTime now = LocalDateTime.now();

        // 鏍规嵁瀵瑰簲鐨勬搷浣滅被鍨嬶紝鍒╃敤鍙嶅皠涓哄搴旂殑瀛楁璧嬪€?        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(entity, now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

