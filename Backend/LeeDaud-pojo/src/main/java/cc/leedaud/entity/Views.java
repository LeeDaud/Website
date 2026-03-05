package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 娴忚
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Views implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 璁垮ID
    private Long visitorId;

    // 椤甸潰璺緞
    private String pagePath;

    // 鏉ユ簮URL
    private String referer;

    // 椤甸潰鏍囬
    private String pageTitle;

    // IP鍦板潃
    private String ipAddress;

    // 鐢ㄦ埛浠ｇ悊
    private String userAgent;

    // 璁块棶鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewTime;
}

