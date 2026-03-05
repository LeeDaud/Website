package cc.leedaud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鏂囩珷-鏍囩鍏宠仈
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagRelations implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鏂囩珷ID
    private Long articleId;

    // 鏍囩ID
    private Long tagId;
}

