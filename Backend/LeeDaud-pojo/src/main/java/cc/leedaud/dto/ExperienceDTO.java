package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 缁忓巻DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO implements Serializable {

    private Long id;

    // 绫诲瀷锛?-鏁欒偛缁忓巻锛?-瀹炰範鍙婂伐浣滅粡鍘?2-椤圭洰缁忓巻
    @NotNull(message = "缁忓巻绫诲瀷涓嶈兘涓虹┖")
    private Integer type;

    // 鏍囬,鍏徃鍚?瀛︽牎鍚?椤圭洰鍚?    @NotBlank(message = "鏍囬涓嶈兘涓虹┖")
    @Size(max = 50, message = "鏍囬涓嶈兘瓒呰繃50瀛?)
    private String title;

    // 鍓爣棰?鑱屼綅/涓撲笟/椤圭洰瑙掕壊
    @Size(max = 100, message = "鍓爣棰樹笉鑳借秴杩?00瀛?)
    private String subtitle;

    // logo
    private String logoUrl;

    // 鍐呭
    @NotBlank(message = "鍐呭涓嶈兘涓虹┖")
    private String content;

    // 椤圭洰閾炬帴锛堜富瑕佺敤浜庨」鐩粡鍘嗭級
    @Size(max = 255, message = "椤圭洰閾炬帴涓嶈兘瓒呰繃255瀛?)
    private String projectLink;

    // 寮€濮嬫椂闂?    @NotNull(message = "寮€濮嬫椂闂翠笉鑳戒负绌?)
    private LocalDate startDate;

    // 缁撴潫鏃堕棿
    private LocalDate endDate;

    // 鏄惁鍙
    private Integer isVisible;
}

