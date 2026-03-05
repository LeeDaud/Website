package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鐪佷唤璁垮鏁伴噺DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceCountDTO {

    // 鐪佷唤
    private String province;

    // 璁垮鏁伴噺
    private Integer count;
}

