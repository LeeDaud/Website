package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鏂囩珷鏍囬涓庢祻瑙堥噺DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleTitleViewCountDTO {

    // 鏂囩珷鏍囬
    private String title;

    // 娴忚閲?    private Integer viewCount;
}

