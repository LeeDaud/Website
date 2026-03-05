package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鏂囩珷鍒嗛〉鏌ヨDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticlePageQueryDTO {

    // 椤电爜
    private int page;

    // 姣忛〉鏄剧ず鏁伴噺
    private int pageSize;

    // 鏂囩珷鏍囬锛堟ā绯婃悳绱級
    private String title;

    // 鍒嗙被ID
    private Long categoryId;

    // 鏄惁鍙戝竷,0-鑽夌锛?-宸插彂甯?    private Integer isPublished;
}

