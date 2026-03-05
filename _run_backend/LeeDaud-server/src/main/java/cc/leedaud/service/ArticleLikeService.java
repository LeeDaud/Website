package cc.leedaud.service;

public interface ArticleLikeService {

    /**
     * 鐐硅禐鏂囩珷
     */
    void likeArticle(Long articleId, Long visitorId);

    /**
     * 鍙栨秷鐐硅禐
     */
    void unlikeArticle(Long articleId, Long visitorId);

    /**
     * 妫€鏌ユ槸鍚﹀凡鐐硅禐
     */
    boolean hasLiked(Long articleId, Long visitorId);
}

