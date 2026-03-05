package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRecordVO {
    // 缁欏墠绔繑鍥炶瀹㈡爣璇?
    // 璁惧鎸囩汗
    private String visitorFingerprint;
    // 褰撳墠浼氳瘽ID
    private String sessionId;
    // 璁垮鍦ㄦ暟鎹簱涓殑ID
    private Long visitorId;
    // 鏄惁鏄柊璁垮
    private Boolean isNewVisitor;
}

