package cc.leedaud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鏂囩珷璇勮鍒嗛〉鏌ヨDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentPageQueryDTO implements Serializable {

    // 椤电爜
    private Integer page;

    // 姣忛〉鏄剧ず鏁伴噺
    private Integer pageSize;

    // 鏂囩珷ID
    private Long articleId;

    // 鏄惁瀹℃牳閫氳繃锛?-鍚︼紝1-鏄?    private Integer isApproved;

    // 寮€濮嬫椂闂?    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 缁撴潫鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

