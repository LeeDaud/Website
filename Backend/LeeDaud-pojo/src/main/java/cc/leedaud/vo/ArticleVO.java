package cc.leedaud.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鏂囩珷鍒楄〃VO锛堜笉鍚鏂囧唴瀹癸級
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鏂囩珷鏍囬
    private String title;

    // URL鏍囪瘑
    private String slug;

    // 鏂囩珷鎽樿
    private String summary;

    // 灏侀潰鍥剧墖url
    private String coverImage;

    // 鍒嗙被ID
    private Long categoryId;

    // 鍒嗙被鍚嶇О
    private String categoryName;

    // 娴忚娆℃暟
    private Long viewCount;

    // 鐐硅禐娆℃暟
    private Long likeCount;

    // 璇勮鏁?    private Long commentCount;

    // 瀛楁暟缁熻
    private Long wordCount;

    // 棰勮闃呰鏃堕棿锛堝垎閽燂級
    private Long readingTime;

    // 鏄惁鍙戝竷
    private Integer isPublished;

    // 鏄惁缃《
    private Integer isTop;

    // 鍙戝竷鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

