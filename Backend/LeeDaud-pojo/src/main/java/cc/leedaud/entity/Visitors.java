package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 璁垮
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visitors implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 璁垮鎸囩汗,鐢ㄤ簬鍞竴鏍囪瘑璁垮
    private String fingerprint;

    // 浼氳瘽ID(褰撳墠娴忚鍣ㄤ細璇?
    private String sessionId;

    // IP鍦板潃
    private String ip;

    // 鐢ㄦ埛浠ｇ悊
    private String userAgent;

    // 鍥藉
    private String country;

    // 鐪佷唤
    private String province;

    // 鍩庡競
    private String city;

    // 缁忓害
    private String longitude;

    // 绾害
    private String latitude;

    // 棣栨璁块棶鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstVisitTime;

    // 鏈€鍚庤闂椂闂?    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastVisitTime;

    // 璁块棶娆℃暟
    private Long totalViews;

    // 鏄惁琚皝绂?0-鍚︼紝1-鏄?    private Integer isBlocked;

    // 灏佺缁撴潫鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

