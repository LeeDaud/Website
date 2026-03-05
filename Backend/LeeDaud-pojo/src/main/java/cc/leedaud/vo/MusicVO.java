package cc.leedaud.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicVO {
    
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
}

