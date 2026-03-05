package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 绯荤粺閰嶇疆DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDTO implements Serializable {

    private Long id;

    // 閰嶇疆閿?    @NotBlank(message = "閰嶇疆閿笉鑳戒负绌?)
    @Size(max = 50, message = "閰嶇疆閿笉鑳借秴杩?0瀛?)
    private String configKey;

    // 閰嶇疆鍊?    private String configValue;

    // 閰嶇疆绫诲瀷
    @Size(max = 20, message = "閰嶇疆绫诲瀷涓嶈兘瓒呰繃20瀛?)
    private String configType;

    // 閰嶇疆鎻忚堪
    @Size(max = 255, message = "閰嶇疆鎻忚堪涓嶈兘瓒呰繃255瀛?)
    private String description;
}

