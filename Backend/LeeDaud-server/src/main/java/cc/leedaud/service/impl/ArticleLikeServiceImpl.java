package cc.leedaud.service.impl;

import cc.leedaud.entity.ArticleLikes;
import cc.leedaud.mapper.ArticleLikeMapper;
import cc.leedaud.mapper.ArticleMapper;
import cc.leedaud.service.ArticleLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Transactional
    public void likeArticle(Long articleId, Long visitorId) {
        // 妫€鏌ユ槸鍚﹀凡缁忕偣璧?        int count = articleLikeMapper.countByArticleIdAndVisitorId(articleId, visitorId);
        if (count > 0) {
            return;
        }
        // 鎻掑叆鐐硅禐璁板綍
        ArticleLikes articleLikes = ArticleLikes.builder()
                .articleId(articleId)
                .visitorId(visitorId)
                .likeTime(LocalDateTime.now())
                .build();
        articleLikeMapper.insert(articleLikes);
        // 鏂囩珷鐐硅禐鏁?1
        articleMapper.incrementLikeCount(articleId);
    }

    @Transactional
    public void unlikeArticle(Long articleId, Long visitorId) {
        // 妫€鏌ユ槸鍚﹀凡缁忕偣璧?        int count = articleLikeMapper.countByArticleIdAndVisitorId(articleId, visitorId);
        if (count == 0) {
            return;
        }
        // 鍒犻櫎鐐硅禐璁板綍
        articleLikeMapper.deleteByArticleIdAndVisitorId(articleId, visitorId);
        // 鏂囩珷鐐硅禐鏁?1
        articleMapper.decrementLikeCount(articleId);
    }

    public boolean hasLiked(Long articleId, Long visitorId) {
        return articleLikeMapper.countByArticleIdAndVisitorId(articleId, visitorId) > 0;
    }
}

