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
 * 绠＄悊鍛樺洖澶嶆枃绔犺瘎璁篋TO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentReplyDTO implements Serializable {

    // 鏂囩珷ID
    @NotNull(message = "鏂囩珷ID涓嶈兘涓虹┖")
    private Long articleId;

    // 鐖惰瘎璁篒D
    @NotNull(message = "鐖惰瘎璁篒D涓嶈兘涓虹┖")
    private Long parentId;

    // 鏍硅瘎璁篒D
    private Long rootId;

    // 鐖惰瘎璁烘樀绉?    @Size(max = 30, message = "鐖惰瘎璁烘樀绉颁笉鑳借秴杩?0瀛?)
    private String parentNickname;

    // 鍥炲鍐呭
    @NotBlank(message = "鍥炲鍐呭涓嶈兘涓虹┖")
    @Size(max = 2000, message = "鍥炲鍐呭涓嶈兘瓒呰繃2000瀛?)
    private String content;

    // 鏄惁浣跨敤markdown锛?-鍚︼紝1-鏄?    private Integer isMarkdown;
}

