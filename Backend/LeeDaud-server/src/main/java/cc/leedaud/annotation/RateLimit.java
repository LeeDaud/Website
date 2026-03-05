package cc.leedaud.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 闄愭祦娉ㄨВ
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 闄愭祦绫诲瀷
     */
    Type type() default Type.IP;

    /**
     * 姣忕浠ょ墝鏁?     */
    double tokens() default 10.0;

    /**
     * 绐佸彂瀹归噺
     */
    int burstCapacity() default 20;

    /**
     * 鏃堕棿绐楀彛
     */
    int timeWindow() default 1;

    /**
     * 鏃堕棿鍗曚綅
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 闄愭祦鎻愮ず娑堟伅
     */
    String message() default "璇锋眰杩囦簬棰戠箒锛岃绋嶅悗鍐嶈瘯";

    enum Type {
        IP,        // IP闄愭祦
        USER,      // 鐢ㄦ埛闄愭祦
        GLOBAL,    // 鍏ㄥ眬闄愭祦
        ENDPOINT   // 鎺ュ彛闄愭祦
    }
}

