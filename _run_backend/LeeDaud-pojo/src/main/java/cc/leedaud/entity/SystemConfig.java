package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 绯荤粺閰嶇疆
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 閰嶇疆閿?    private String configKey;

    // 閰嶇疆鍊?    private String configValue;

    // 閰嶇疆绫诲瀷
    private String configType;

    // 閰嶇疆鎻忚堪
    private String description;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

