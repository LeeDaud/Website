package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 绀句氦濯掍綋DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaDTO implements Serializable {

    private Long id;

    // 鍚嶇О
    @NotBlank(message = "鍚嶇О涓嶈兘涓虹┖")
    @Size(max = 20, message = "鍚嶇О涓嶈兘瓒呰繃20瀛?)
    private String name;

    // 鍥炬爣绫诲悕
    @Size(max = 50, message = "鍥炬爣绫诲悕涓嶈兘瓒呰繃50瀛?)
    private String icon;

    // 閾炬帴
    @Size(max = 100, message = "閾炬帴涓嶈兘瓒呰繃100瀛?)
    private String link;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

    // 鏄惁鍙
    private Integer isVisible;
}

