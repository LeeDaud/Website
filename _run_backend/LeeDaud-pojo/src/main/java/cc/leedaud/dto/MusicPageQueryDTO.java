package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MusicPageQueryDTO {

    // 椤电爜
    private int page;

    // 姣忛〉鏄剧ず鏁伴噺
    private int pageSize;

    // 闊充箰鏍囬
    private String title;

    // 浣滆€?    private String artist;

    // 鏄惁鍙
    private Integer isVisible;
}

