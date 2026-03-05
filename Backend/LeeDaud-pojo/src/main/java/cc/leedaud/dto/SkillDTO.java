package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鎶€鑳紻TO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO implements Serializable {

    private Long id;

    // 鎶€鑳藉悕绉?    @NotBlank(message = "鎶€鑳藉悕绉颁笉鑳戒负绌?)
    @Size(max = 20, message = "鎶€鑳藉悕绉颁笉鑳借秴杩?0瀛?)
    private String name;

    // 鎶€鑳芥弿杩?    @Size(max = 255, message = "鎶€鑳芥弿杩颁笉鑳借秴杩?55瀛?)
    private String description;

    // 鍥炬爣url
    private String icon;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

    // 鏄惁鍙
    private Integer isVisible;
}

