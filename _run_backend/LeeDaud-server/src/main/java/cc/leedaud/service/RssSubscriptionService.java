package cc.leedaud.service;

import cc.leedaud.dto.RssSubscriptionDTO;
import cc.leedaud.dto.RssSubscriptionPageQueryDTO;
import cc.leedaud.entity.RssSubscriptions;
import cc.leedaud.result.PageResult;
import cc.leedaud.vo.RssSubscriptionStatusVO;

import java.util.List;

public interface RssSubscriptionService {
    /**
     * 娣诲姞RSS璁㈤槄
     * @param rssSubscriptionDTO
     */
    void addSubscription(RssSubscriptionDTO rssSubscriptionDTO);

    /**
     * 鍒嗛〉鏌ヨRSS璁㈤槄鍒楄〃
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO);

    /**
     * 鏇存柊RSS璁㈤槄
     * @param rssSubscriptions
     */
    void updateSubscription(RssSubscriptions rssSubscriptions);

    /**
     * 鎵归噺鍒犻櫎RSS璁㈤槄
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 鏍规嵁ID鏌ヨRSS璁㈤槄
     * @param id
     * @return
     */
    RssSubscriptions getById(Long id);

    /**
     * 鑾峰彇鎵€鏈夋縺娲荤殑璁㈤槄
     * @return
     */
    List<RssSubscriptions> getAllActiveSubscriptions();

    /**
     * 鏍规嵁閭鍙栨秷璁㈤槄
     * @param email
     */
    void unsubscribeByEmail(String email);

    /**
     * 妫€鏌ヨ瀹㈡槸鍚﹀凡璁㈤槄
     * @param visitorId
     * @return
     */
    boolean hasSubscribed(Long visitorId);

    /**
     * 鑾峰彇璁垮璁㈤槄璇︽儏
     * @param visitorId
     * @return
     */
    RssSubscriptionStatusVO getSubscriptionStatus(Long visitorId);
}

