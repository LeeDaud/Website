package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鍙嬫儏閾炬帴
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendLinks implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 缃戠珯鍚嶇О
    private String name;

    // 缃戠珯鍦板潃
    private String url;

    // 澶村儚url
    private String avatarUrl;

    // 缃戠珯鎻忚堪
    private String description;

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

