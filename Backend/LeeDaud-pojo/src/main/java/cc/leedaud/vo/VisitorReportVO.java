package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 璁垮缁熻VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitorReportVO {

    // 鏃ユ湡锛屼互閫楀彿鍒嗛殧锛屼緥濡傦細2025-01-01,2025-01-02
    private String dateList;

    // 鏂板璁垮鏁帮紝浠ラ€楀彿鍒嗛殧锛屼緥濡傦細5,12,8
    private String newVisitorCountList;

    // 绱璁垮鏁帮紝浠ラ€楀彿鍒嗛殧锛屼緥濡傦細5,17,25
    private String totalVisitorCountList;
}

