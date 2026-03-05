package cc.leedaud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鐣欒█鍒嗛〉鏌ヨDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageQueryDTO implements Serializable {

    private Integer page;

    private Integer pageSize;

    // 鏄惁瀹℃牳閫氳繃锛?-鍚︼紝1-鏄?    private Integer isApproved;

    // 寮€濮嬫椂闂?    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 缁撴潫鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

