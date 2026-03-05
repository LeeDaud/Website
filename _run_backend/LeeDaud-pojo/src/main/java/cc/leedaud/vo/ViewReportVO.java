package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 娴忚閲忕粺璁O
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewReportVO {

    // 鏃ユ湡锛屼互閫楀彿鍒嗛殧锛屼緥濡傦細2025-01-01,2025-01-02
    private String dateList;

    // 娴忚閲忥紝浠ラ€楀彿鍒嗛殧锛屼緥濡傦細120,350,200
    private String viewCountList;
}

