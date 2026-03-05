package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RssSubscriptionPageQueryDTO {

    // 椤电爜
    private int page;

    // 姣忛〉鏄剧ず鏁伴噺
    private int pageSize;

    // 閭
    private String email;

    // 鏄惁婵€娲伙紝0-鍚︼紝1-鏄?    private Integer isActive;
}

