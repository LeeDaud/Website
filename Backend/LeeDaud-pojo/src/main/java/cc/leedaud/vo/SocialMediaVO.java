package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaVO {
    private Long id;

    // 鍚嶇О
    private String name;

    // 鍥炬爣绫诲悕
    private String icon;

    // 閾炬帴
    private String link;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;
}

