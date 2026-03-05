package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 姣忔棩鏂板璁垮缁熻DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyVisitorCountDTO {

    // 鏃ユ湡
    private LocalDate date;

    // 褰撴棩鏂板璁垮鏁?    private Integer count;
}

