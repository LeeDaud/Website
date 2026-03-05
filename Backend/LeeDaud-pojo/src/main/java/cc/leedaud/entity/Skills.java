package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鎶€鑳? */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skills implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鎶€鑳藉悕绉?    private String name;

    // 鎶€鑳芥弿杩?    private String description;

    // 鍥炬爣url
    private String icon;

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

