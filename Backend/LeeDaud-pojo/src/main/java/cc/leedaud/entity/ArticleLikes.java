package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鏂囩珷鐐硅禐
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLikes implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鏂囩珷ID
    private Long articleId;

    // 璁垮ID
    private Long visitorId;

    // 鐐硅禐鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime likeTime;
}

