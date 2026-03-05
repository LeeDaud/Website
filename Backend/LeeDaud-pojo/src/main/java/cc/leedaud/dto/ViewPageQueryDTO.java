package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewPageQueryDTO {

    // 椤电爜
    private int page;

    // 姣忛〉鏄剧ず鏁伴噺
    private int pageSize;

    // 椤甸潰璺緞
    private String pagePath;

    // 鏉ユ簮URL
    private String referer;

    // 璁垮ID
    private Long visitorId;
}

