package cc.leedaud.annotation;

import cc.leedaud.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 鑷畾涔夋敞瑙ｏ紝鐢ㄤ簬鏍囪瘑鏂规硶闇€瑕佽繘琛屽姛鑳藉瓧娈电殑鑷姩濉厖
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 鏁版嵁搴撴搷浣滅被鍨? INSERT, UPDATE
    OperationType value();
}

