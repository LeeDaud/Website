package cc.leedaud.annotation;

import cc.leedaud.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鎿嶄綔鏃ュ織娉ㄨВ锛屾爣璇嗙鐞嗗憳鐨勫鍒犳敼鎿嶄綔
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 鎿嶄綔绫诲瀷锛圛NSERT, UPDATE, DELETE锛?     */
    OperationType value();

    /**
     * 鎿嶄綔鐩爣/妯″潡锛坥peration_target锛?     */
    String target() default "";

    /**
     * 鐩爣ID鐨勮〃杈惧紡锛圫pEL琛ㄨ揪寮忥紝浠庡弬鏁颁腑鎻愬彇锛?     * 渚嬪: "#request.id" 鎴?"#id"
     */
    String targetId() default "";

    /**
     * 鏄惁璁板綍鎿嶄綔鏁版嵁
     */
    boolean saveData() default true;
}

