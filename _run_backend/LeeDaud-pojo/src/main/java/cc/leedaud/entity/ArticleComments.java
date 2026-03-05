package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鏂囩珷璇勮
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComments implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鏂囩珷ID
    private Long articleId;

    // 鏍硅瘎璁篒D,null鏄竴绾ц瘎璁?    private Long rootId;

    // 鐖惰瘎璁篒D,null鏄竴绾ц瘎璁?    private Long parentId;

    // 鐖惰瘎璁烘樀绉?    private String parentNickname;

    // 璇勮鍐呭
    private String content;

    // 杞崲鍚庣殑HTML鍐呭
    private String contentHtml;

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

    // 鏂囩珷鏍囬锛堥潪鏁版嵁搴撳瓧娈碉紝鍏宠仈鏌ヨ鏃跺～鍏咃級
    private String articleTitle;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

