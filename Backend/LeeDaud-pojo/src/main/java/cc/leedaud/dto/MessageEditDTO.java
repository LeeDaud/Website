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
 * 璁垮缂栬緫鐣欒█DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEditDTO implements Serializable {

    // 鐣欒█ID
    @NotNull(message = "鐣欒█ID涓嶈兘涓虹┖")
    private Long id;

    // 璁垮ID锛堢敤浜庨獙璇佽韩浠斤級
    @NotNull(message = "璁垮ID涓嶈兘涓虹┖")
    private Long visitorId;

    // 缂栬緫鍚庣殑鍐呭
    @NotBlank(message = "鐣欒█鍐呭涓嶈兘涓虹┖")
    @Size(max = 2000, message = "鐣欒█鍐呭涓嶈兘瓒呰繃2000瀛?)
    private String content;

    // 鏄惁浣跨敤markdown
    private Integer isMarkdown;
}

