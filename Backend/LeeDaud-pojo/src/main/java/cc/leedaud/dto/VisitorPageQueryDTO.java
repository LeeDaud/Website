package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitorPageQueryDTO {

    // 椤电爜
    private int page;

    // 姣忛〉鏄剧ず鏁伴噺
    private int pageSize;

    // 鍥藉
    private String country;

    // 鐪佷唤
    private String province;

    // 鍩庡競
    private String city;

    // 鐘舵€?鏄惁琚皝绂?0姝ｅ父 1灏佺
    private Integer status;
}

