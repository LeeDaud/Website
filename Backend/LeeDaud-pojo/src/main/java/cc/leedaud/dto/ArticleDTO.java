package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 鏂囩珷鍒涘缓/鏇存柊DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDTO {

    // 鏂囩珷ID锛堟洿鏂版椂浣跨敤锛?    private Long id;

    // 鏂囩珷鏍囬
    @NotBlank(message = "鏂囩珷鏍囬涓嶈兘涓虹┖")
    @Size(max = 50, message = "鏂囩珷鏍囬涓嶈兘瓒呰繃50瀛?)
    private String title;

    // URL鏍囪瘑
    @NotBlank(message = "URL鏍囪瘑涓嶈兘涓虹┖")
    @Size(max = 50, message = "URL鏍囪瘑涓嶈兘瓒呰繃50瀛?)
    private String slug;

    // 鏂囩珷鎽樿
    private String summary;

    // 灏侀潰鍥剧墖url
    private String coverImage;

    // Markdown鍐呭
    @NotBlank(message = "鏂囩珷鍐呭涓嶈兘涓虹┖")
    private String contentMarkdown;

    // 鍓嶇缂栬緫鍣ㄦ覆鏌撶殑HTML鍐呭锛堝彲閫夛紝鑻ユ彁渚涘垯鐩存帴浣跨敤锛屼笉鍐嶅悗绔浆鎹級
    private String contentHtml;

    // 鍒嗙被ID
    @NotNull(message = "鏂囩珷鍒嗙被涓嶈兘涓虹┖")
    private Long categoryId;

    // 鏄惁鍙戝竷,0-鍚︼紙鑽夌锛夛紝1-鏄?    private Integer isPublished;

    // 鏄惁缃《,0-鍚︼紝1-鏄?    private Integer isTop;

    // 鏍囩ID鍒楄〃
    private List<Long> tagIds;
}

