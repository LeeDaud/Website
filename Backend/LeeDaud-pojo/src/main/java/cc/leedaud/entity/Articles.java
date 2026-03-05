package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 鏂囩珷
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Articles implements Serializable {

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

    // Markdown鍐呭
    private String contentMarkdown;

    // 杞崲鍚庣殑HTML鍐呭
    private String contentHtml;

    // 鍒嗙被ID
    private Long categoryId;

    // 娴忚娆℃暟
    private Long viewCount;

    // 鐐硅禐娆℃暟
    private Long likeCount;

    // 璇勮鏁?    private Long commentCount;

    // 瀛楁暟缁熻
    private Long wordCount;

    // 棰勮闃呰鏃堕棿锛屽崟浣嶏細鍒嗛挓
    private Long readingTime;

    // 鏄惁鍙戝竷,0-鍚︼紝1-鏄?    private Integer isPublished;

    // 鏄惁缃《,0-鍚︼紝1-鏄?    private Integer isTop;

    // 鍙戝竷鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    // 鍙戝竷骞翠唤
    private Integer publishYear;

    // 鍙戝竷鏈堜唤
    private Integer publishMonth;

    // 鍙戝竷鏃ユ湡
    private Integer publishDay;

    // 鍙戝竷鏃ユ湡锛堝幓鎺夋椂闂达級
    private LocalDate publishDate;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 鏍囩ID鍒楄〃锛堥潪鏁版嵁搴撳瓧娈碉紝绠＄悊绔繑鍥炴椂濉厖锛?    private List<Long> tagIds;
}

