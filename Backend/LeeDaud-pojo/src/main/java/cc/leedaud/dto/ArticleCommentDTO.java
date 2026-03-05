package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鍗氬绔瀹㈡彁浜ゆ枃绔犺瘎璁篋TO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDTO implements Serializable {

    @NotNull(message = "鏂囩珷ID涓嶈兘涓虹┖")
    private Long articleId;

    private Long rootId;
    private Long parentId;

    @Size(max = 15, message = "鐖惰瘎璁烘樀绉颁笉鑳借秴杩?5瀛?)
    private String parentNickname;

    @NotBlank(message = "璇勮鍐呭涓嶈兘涓虹┖")
    @Size(max = 2000, message = "璇勮鍐呭涓嶈兘瓒呰繃2000瀛?)
    private String content;

    @NotNull(message = "璁垮ID涓嶈兘涓虹┖")
    private Long visitorId;

    @NotBlank(message = "鏄电О涓嶈兘涓虹┖")
    @Size(max = 15, message = "鏄电О涓嶈兘瓒呰繃15瀛?)
    private String nickname;

    @Size(max = 50, message = "閭鎴朡Q鍙蜂笉鑳借秴杩?0瀛?)
    private String emailOrQq;

    private Integer isMarkdown;
    private Integer isSecret;
    private Integer isNotice;
}

