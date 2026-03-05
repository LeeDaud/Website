package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 璁垮璁块棶璁板綍DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRecordDTO {

    // 璁块棶璺緞
    @NotBlank(message = "璁块棶璺緞涓嶈兘涓虹┖")
    @Size(max = 500, message = "璁块棶璺緞杩囬暱")
    private String pagePath;

    // 椤甸潰鏍囬
    @Size(max = 200, message = "椤甸潰鏍囬杩囬暱")
    private String pageTitle;

    // 鏉ユ簮椤甸潰
    @Size(max = 500, message = "鏉ユ簮椤甸潰杩囬暱")
    private String referer;

    // 鍓嶇鏀堕泦娴忚鍣ㄤ俊鎭?鐢ㄤ簬鍒朵綔璁垮鎸囩汗

    // 灞忓箷鍒嗚鲸鐜?"1920x1080"
    private String screen;
    // 鏃跺尯 "Asia/Shanghai"
    private String timezone;
    // 璇█ "zh-CN"
    private String language;
    // 骞冲彴 "Win32"
    private String platform;
    // 鏄惁鏀寔Cookie
    private Boolean cookiesEnabled;

    // 璁惧淇℃伅

    // 璁惧鍐呭瓨
    private Integer deviceMemory;
    // CPU鏍稿績鏁?    private Integer hardwareConcurrency;
}

