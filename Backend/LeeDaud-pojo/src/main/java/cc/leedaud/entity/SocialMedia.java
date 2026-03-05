package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 绀句氦濯掍綋
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鍚嶇О
    private String name;

    // 鍥炬爣绫诲悕
    private String icon;

    // 閾炬帴
    private String link;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

    // 鏄惁鍙
    private Integer isVisible;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

