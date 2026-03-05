package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鐣欒█
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Messages implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 璇勮鍐呭
    private String content;

    // 杞崲鍚庣殑HTML鍐呭
    private String contentHtml;

    // 鏍圭暀瑷€ID,null鏄竴绾х暀瑷€
    private Long rootId;

    // 鐖剁暀瑷€ID,null鏄竴绾х暀瑷€
    private Long parentId;

    // 鐖剁暀瑷€鏄电О
    private String parentNickname;

    // 璁垮ID
    private Long visitorId;

    // 鏄电О
    private String nickname;

    // 閭鎴杚q
    private String emailOrQq;

    // 鍦板潃
    private String location;

    // 鎿嶄綔绯荤粺鍚嶇О
    private String userAgentOs;

    // 娴忚鍣ㄥ悕绉?    private String userAgentBrowser;

    // 鏄惁瀹℃牳閫氳繃锛?-鍚︼紝1-鏄?    private Integer isApproved;

    // 鏄惁浣跨敤markdown锛?-鍚︼紝1-鏄?    private Integer isMarkdown;

    // 鏄惁鍖垮悕锛?-鍚︼紝1-鏄?    private Integer isSecret;

    // 鏈夊洖澶嶆槸鍚﹂€氱煡锛?-鍚︼紝1-鏄?    private Integer isNotice;

    // 鏄惁缂栬緫杩囷紝0-鍚︼紝1-鏄?    private Integer isEdited;

    // 鏄惁涓虹鐞嗗憳鍥炲锛?-鍚︼紝1-鏄?    private Integer isAdminReply;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

