package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 鏂囩珷璁块棶閲忔帓琛屽墠鍗乂O
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleViewTop10VO {

    // 鏂囩珷鏍囬鍒楄〃
    private List<String> titleList;

    // 瀵瑰簲鏂囩珷鐨勬祻瑙堥噺鍒楄〃
    private List<Integer> viewCountList;
}

