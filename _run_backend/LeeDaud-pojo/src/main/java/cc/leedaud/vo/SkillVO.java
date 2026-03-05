package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillVO implements Serializable {

    private Long id;

    // 鎶€鑳藉悕绉?    private String name;

    // 鎶€鑳芥弿杩?    private String description;

    // 鍥炬爣url
    private String icon;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

}

