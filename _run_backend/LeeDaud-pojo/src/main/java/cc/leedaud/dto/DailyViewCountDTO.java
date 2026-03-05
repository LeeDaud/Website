package cc.leedaud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 姣忔棩娴忚閲忕粺璁TO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyViewCountDTO {

    // 鏃ユ湡
    private LocalDate date;

    // 褰撴棩娴忚閲?    private Integer count;
}

