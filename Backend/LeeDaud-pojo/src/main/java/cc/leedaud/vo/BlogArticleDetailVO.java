package cc.leedaud.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 鍗氬绔枃绔犺鎯匳O锛堝惈HTML鍐呭锛? */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String coverImage;
    private String contentHtml;
    private String contentMarkdown;
    private Long categoryId;
    private String categoryName;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long wordCount;
    private Long readingTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 鏂囩珷鏍囩鍚嶇О鍒楄〃
    private List<String> tagNames;

    // 涓婁竴绡?涓嬩竴绡囧鑸?    private BlogArticleVO prevArticle;
    private BlogArticleVO nextArticle;

    // 鐩稿叧鏂囩珷鎺ㄨ崘
    private List<BlogArticleVO> relatedArticles;
}

