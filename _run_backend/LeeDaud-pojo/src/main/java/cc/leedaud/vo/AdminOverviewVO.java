package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 绠＄悊绔€昏缁熻VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminOverviewVO {

    // 鎬绘祻瑙堥噺
    private Integer totalViewCount;

    // 鎬昏瀹㈡暟
    private Integer totalVisitorCount;

    // 浠婃棩娴忚閲?    private Integer todayViewCount;

    // 浠婃棩鏂板璁垮鏁?    private Integer todayNewVisitorCount;

    // 鎬绘枃绔犳暟
    private Integer totalArticleCount;

    // 鎬昏瘎璁烘暟
    private Integer totalCommentCount;

    // 鎬荤暀瑷€鏁?    private Integer totalMessageCount;

    // 寰呭鏍歌瘎璁烘暟
    private Integer pendingCommentCount;

    // 寰呭鏍哥暀瑷€鏁?    private Integer pendingMessageCount;
}

