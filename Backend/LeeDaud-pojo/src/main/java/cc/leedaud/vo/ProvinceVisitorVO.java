package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 璁垮鐪佷唤鍒嗗竷缁熻VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceVisitorVO {

    // 鐪佷唤锛屼互閫楀彿鍒嗛殧锛屼緥濡傦細骞夸笢,鍖椾含,娴欐睙
    private String provinceList;

    // 瀵瑰簲鐪佷唤鐨勮瀹㈡暟锛屼互閫楀彿鍒嗛殧锛屼緥濡傦細120,85,60
    private String countList;
}

