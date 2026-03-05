package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 鏂囩珷褰掓。VO锛堟寜骞存湀鍒嗙粍锛? */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleArchiveVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer year;
    private Integer month;
    private List<ArticleArchiveItemVO> articles;
}

