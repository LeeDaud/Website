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
 * 绠＄悊鍛樺洖澶嶇暀瑷€DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReplyDTO implements Serializable {

    // 鐖剁暀瑷€ID
    @NotNull(message = "鐖剁暀瑷€ID涓嶈兘涓虹┖")
    private Long parentId;

    // 鏍圭暀瑷€ID
    private Long rootId;

    // 鐖剁暀瑷€鏄电О
    @Size(max = 30, message = "鐖剁暀瑷€鏄电О涓嶈兘瓒呰繃30瀛?)
    private String parentNickname;

    // 鍥炲鍐呭
    @NotBlank(message = "鍥炲鍐呭涓嶈兘涓虹┖")
    @Size(max = 2000, message = "鍥炲鍐呭涓嶈兘瓒呰繃2000瀛?)
    private String content;

    // 鏄惁浣跨敤markdown锛?-鍚︼紝1-鏄?    private Integer isMarkdown;
}

