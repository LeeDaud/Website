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
 * 鐣欒█VO锛堟爲褰㈢粨鏋勶級
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long rootId;
    private Long parentId;
    private String parentNickname;
    private String content;
    private String contentHtml;
    private Integer isMarkdown;
    private Long visitorId;
    private String nickname;
    private String emailOrQq;
    private String location;
    private String userAgentOs;
    private String userAgentBrowser;
    private Integer isApproved;
    private Integer isSecret;
    private Integer isAdminReply;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 瀛愮暀瑷€鍒楄〃锛堜粎鏍圭暀瑷€鏈夊€硷級
     */
    private List<MessageVO> children;
}

