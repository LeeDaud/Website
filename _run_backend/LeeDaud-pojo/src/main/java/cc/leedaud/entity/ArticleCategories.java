package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鏂囩珷鍒嗙被
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategories implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鍒嗙被鍚嶇О
    private String name;

    // URL鏍囪瘑
    private String slug;

    // 鍒嗙被鎻忚堪
    private String description;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

    // 鏂囩珷鏁伴噺锛堥潪鏁版嵁搴撳瓧娈碉紝鏌ヨ鏃惰绠楋級
    private Integer articleCount;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

