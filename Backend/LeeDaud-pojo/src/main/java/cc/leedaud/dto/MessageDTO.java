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
 * 璁垮鎻愪氦鐣欒█DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    // 鐣欒█鍐呭
    @NotBlank(message = "鐣欒█鍐呭涓嶈兘涓虹┖")
    @Size(max = 2000, message = "鐣欒█鍐呭涓嶈兘瓒呰繃2000瀛?)
    private String content;

    // 鏍圭暀瑷€ID,null鏄竴绾х暀瑷€
    private Long rootId;

    // 鐖剁暀瑷€ID,null鏄竴绾х暀瑷€
    private Long parentId;

    // 鐖剁暀瑷€鏄电О
    @Size(max = 15, message = "鐖剁暀瑷€鏄电О涓嶈兘瓒呰繃15瀛?)
    private String parentNickname;

    // 璁垮ID
    @NotNull(message = "璁垮ID涓嶈兘涓虹┖")
    private Long visitorId;

    // 鏄电О
    @NotBlank(message = "鏄电О涓嶈兘涓虹┖")
    @Size(max = 15, message = "鏄电О涓嶈兘瓒呰繃15瀛?)
    private String nickname;

    // 閭鎴杚q
    @Size(max = 50, message = "閭鎴朡Q鍙蜂笉鑳借秴杩?0瀛?)
    private String emailOrQq;

    // 鏄惁浣跨敤markdown锛?-鍚︼紝1-鏄?    private Integer isMarkdown;

    // 鏄惁鍖垮悕锛?-鍚︼紝1-鏄?    private Integer isSecret;

    // 鏈夊洖澶嶆槸鍚﹂€氱煡锛?-鍚︼紝1-鏄?    private Integer isNotice;
}

