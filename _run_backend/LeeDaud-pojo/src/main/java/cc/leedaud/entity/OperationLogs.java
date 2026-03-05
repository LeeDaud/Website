package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鎿嶄綔鏃ュ織
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 绠＄悊鍛業D
    private Long adminId;

    // 鎿嶄綔绫诲瀷
    private String operationType;

    // 鎿嶄綔鐩爣
    private String operationTarget;

    // 鐩爣ID
    private Integer targetId;

    // 鎿嶄綔鏁版嵁
    private String operateData;

    // 鎿嶄綔缁撴灉锛?-澶辫触锛?-鎴愬姛
    private Integer result;

    // 閿欒淇℃伅
    private String errorMessage;

    // 鎿嶄綔鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;
}

