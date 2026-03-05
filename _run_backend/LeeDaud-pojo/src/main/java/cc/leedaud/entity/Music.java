package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 闊充箰
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 闊充箰鏍囬
    private String title;

    // 浣滆€?    private String artist;

    // 鏃堕暱锛屽崟浣嶏細绉?    private Integer duration;

    // 灏侀潰鍥剧墖url
    private String coverImage;

    // 闊抽鏂囦欢url
    private String musicUrl;

    // 姝岃瘝鏂囦欢url
    private String lyricUrl;

    // 鏄惁鏈夋瓕璇嶏紝0-鍚︼紝1-鏄?    private Integer hasLyric;

    // 姝岃瘝绫诲瀷,lrc,json,txt
    private String lyricType;

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

