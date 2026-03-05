package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogReportVO {

    // 鎬绘祻瑙堥噺
    private Integer viewTotalCount;

    // 浠婃棩娴忚閲?    private Integer viewTodayCount;

    // 鎬昏瀹㈡暟
    private Integer visitorTotalCount;

    // 鎬绘枃绔犲垎绫绘暟
    private Integer categoryTotalCount;

    // 鎬绘枃绔犳爣绛炬暟
    private Integer tagTotalCount;

    // 鎬绘枃绔犳暟
    private Integer articleTotalCount;
}

