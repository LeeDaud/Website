package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鏂囩珷鍒嗙被DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategoryDTO implements Serializable {

    private Long id;

    // 鍒嗙被鍚嶇О
    @NotBlank(message = "鍒嗙被鍚嶇О涓嶈兘涓虹┖")
    @Size(max = 20, message = "鍒嗙被鍚嶇О涓嶈兘瓒呰繃20瀛?)
    private String name;

    // URL鏍囪瘑
    @NotBlank(message = "URL鏍囪瘑涓嶈兘涓虹┖")
    @Size(max = 20, message = "URL鏍囪瘑涓嶈兘瓒呰繃20瀛?)
    private String slug;

    // 鍒嗙被鎻忚堪
    @Size(max = 100, message = "鍒嗙被鎻忚堪涓嶈兘瓒呰繃100瀛?)
    private String description;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;
}

