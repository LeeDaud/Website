package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鏂囩珷涓婁笅绡囧鑸猇O
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleNavigationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 涓婁竴绡?    private BlogArticleVO prevArticle;

    // 涓嬩竴绡?    private BlogArticleVO nextArticle;
}

