package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Rss璁㈤槄璁板綍
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RssSubscriptions implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 璁垮ID
    private Long visitorId;

    // 鏄电О
    private String nickname;

    // 閭
    private String email;

    // 鏄惁婵€娲伙紝0-鍚︼紝1-鏄?    private Integer isActive;

    // 璁㈤槄鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime subscribeTime;

    // 鍙栨秷璁㈤槄鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime unSubscribeTime;
}

