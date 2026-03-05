package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceVO implements Serializable {

    private Long id;

    // 绫诲瀷锛?-鏁欒偛缁忓巻锛?-瀹炰範鍙婂伐浣滅粡鍘?2-椤圭洰缁忓巻
    private Integer type;

    // 鏍囬,鍏徃鍚?瀛︽牎鍚?椤圭洰鍚?    private String title;

    // 鍓爣棰?鑱屼綅/涓撲笟/椤圭洰瑙掕壊
    private String subtitle;

    // logo
    private String logoUrl;

    // 鍐呭
    private String content;

    // 椤圭洰閾炬帴锛堜富瑕佺敤浜庨」鐩粡鍘嗭級
    private String projectLink;

    // 寮€濮嬫椂闂?    private LocalDate startDate;

    // 缁撴潫鏃堕棿
    private LocalDate endDate;
}

