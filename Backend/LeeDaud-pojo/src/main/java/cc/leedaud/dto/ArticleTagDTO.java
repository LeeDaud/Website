package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鏂囩珷鏍囩DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagDTO implements Serializable {

    private Long id;

    // 鏍囩鍚嶇О
    @NotBlank(message = "鏍囩鍚嶇О涓嶈兘涓虹┖")
    @Size(max = 20, message = "鏍囩鍚嶇О涓嶈兘瓒呰繃20瀛?)
    private String name;

    // URL鏍囪瘑
    @NotBlank(message = "URL鏍囪瘑涓嶈兘涓虹┖")
    @Size(max = 30, message = "URL鏍囪瘑涓嶈兘瓒呰繃30瀛?)
    private String slug;
}

