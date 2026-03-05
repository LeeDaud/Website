package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 闊充箰DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO implements Serializable {

    private Long id;

    // 闊充箰鏍囬
    @NotBlank(message = "闊充箰鏍囬涓嶈兘涓虹┖")
    @Size(max = 50, message = "闊充箰鏍囬涓嶈兘瓒呰繃50瀛?)
    private String title;

    // 浣滆€?    @Size(max = 50, message = "浣滆€呭悕绉颁笉鑳借秴杩?0瀛?)
    private String artist;

    // 鏃堕暱锛屽崟浣嶏細绉?    private Integer duration;

    // 灏侀潰鍥剧墖url
    private String coverImage;

    // 闊抽鏂囦欢url
    @NotBlank(message = "闊抽鏂囦欢涓嶈兘涓虹┖")
    private String musicUrl;

    // 姝岃瘝鏂囦欢url
    private String lyricUrl;

    // 鏄惁鏈夋瓕璇嶏紝0-鍚︼紝1-鏄?    private Integer hasLyric;

    // 姝岃瘝绫诲瀷,lrc,json,txt
    @Size(max = 10, message = "姝岃瘝绫诲瀷涓嶈兘瓒呰繃10瀛?)
    private String lyricType;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

    // 鏄惁鍙
    private Integer isVisible;
}

