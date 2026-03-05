package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.dto.RssSubscriptionDTO;
import cc.leedaud.dto.RssSubscriptionPageQueryDTO;
import cc.leedaud.entity.RssSubscriptions;
import cc.leedaud.exception.BaseException;
import cc.leedaud.exception.RssSubscriptionException;
import cc.leedaud.mapper.RssSubscriptionMapper;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.RssSubscriptionService;
import cc.leedaud.vo.RssSubscriptionStatusVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RssSubscriptionServiceImpl implements RssSubscriptionService {

    @Autowired
    private RssSubscriptionMapper rssSubscriptionMapper;

    /**
     * 娣诲姞RSS璁㈤槄
     * @param rssSubscriptionDTO
     */
    public void addSubscription(RssSubscriptionDTO rssSubscriptionDTO) {
        // 妫€鏌ラ偖绠辨槸鍚﹀凡瀛樺湪
        RssSubscriptions existingSubscription = rssSubscriptionMapper.getByEmail(rssSubscriptionDTO.getEmail());
        if (existingSubscription != null) {
            // 濡傛灉宸插瓨鍦ㄤ笖婵€娲伙紝鎶涘嚭寮傚父
            if (existingSubscription.getIsActive() == 1) {
                throw new RssSubscriptionException(MessageConstant.RssAlreadyExists);
            }
            // 濡傛灉宸插瓨鍦ㄤ絾鏈縺娲伙紝閲嶆柊婵€娲?            existingSubscription.setIsActive(1);
            existingSubscription.setNickname(rssSubscriptionDTO.getNickname());
            existingSubscription.setUnSubscribeTime(null);
            rssSubscriptionMapper.update(existingSubscription);
        } else {
            // 鏂板璁㈤槄
            RssSubscriptions rssSubscriptions = RssSubscriptions.builder()
                    .visitorId(rssSubscriptionDTO.getVisitorId())
                    .nickname(rssSubscriptionDTO.getNickname())
                    .email(rssSubscriptionDTO.getEmail())
                    .isActive(1)
                    .subscribeTime(LocalDateTime.now())
                    .build();
            rssSubscriptionMapper.insert(rssSubscriptions);
        }
    }

    /**
     * 鍒嗛〉鏌ヨRSS璁㈤槄鍒楄〃
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    public PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO) {
        PageHelper.startPage(rssSubscriptionPageQueryDTO.getPage(), rssSubscriptionPageQueryDTO.getPageSize());
        Page<RssSubscriptions> page = rssSubscriptionMapper.pageQuery(rssSubscriptionPageQueryDTO);
        long total = page.getTotal();
        List<RssSubscriptions> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 鏇存柊RSS璁㈤槄
     * @param rssSubscriptions
     */
    public void updateSubscription(RssSubscriptions rssSubscriptions) {
        rssSubscriptionMapper.update(rssSubscriptions);
    }

    /**
     * 鎵归噺鍒犻櫎RSS璁㈤槄
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        rssSubscriptionMapper.batchDelete(ids);
    }

    /**
     * 鏍规嵁ID鏌ヨRSS璁㈤槄
     * @param id
     * @return
     */
    public RssSubscriptions getById(Long id) {
        return rssSubscriptionMapper.getById(id);
    }

    /**
     * 鑾峰彇鎵€鏈夋縺娲荤殑璁㈤槄
     * @return
     */
    public List<RssSubscriptions> getAllActiveSubscriptions() {
        return rssSubscriptionMapper.getAllActiveSubscriptions();
    }

    /**
     * 鏍规嵁閭鍙栨秷璁㈤槄
     * @param email
     */
    public void unsubscribeByEmail(String email) {
        RssSubscriptions subscription = rssSubscriptionMapper.getByEmail(email);
        if (subscription == null) {
            throw new RssSubscriptionException(MessageConstant.RssNotFound);
        }
        if (subscription.getIsActive() == 0) {
            throw new RssSubscriptionException(MessageConstant.RssNotFound);
        }
        subscription.setIsActive(0);
        subscription.setUnSubscribeTime(LocalDateTime.now());
        rssSubscriptionMapper.update(subscription);
    }

    /**
     * 妫€鏌ヨ瀹㈡槸鍚﹀凡璁㈤槄
     */
    public boolean hasSubscribed(Long visitorId) {
        if (visitorId == null) return false;
        return rssSubscriptionMapper.hasActiveByVisitorId(visitorId);
    }

    /**
     * 鑾峰彇璁垮璁㈤槄璇︽儏
     */
    public RssSubscriptionStatusVO getSubscriptionStatus(Long visitorId) {
        if (visitorId == null) {
            return RssSubscriptionStatusVO.builder().subscribed(false).build();
        }
        RssSubscriptions sub = rssSubscriptionMapper.getActiveByVisitorId(visitorId);
        if (sub == null) {
            return RssSubscriptionStatusVO.builder().subscribed(false).build();
        }
        return RssSubscriptionStatusVO.builder()
                .subscribed(true)
                .nickname(sub.getNickname())
                .email(sub.getEmail())
                .build();
    }
}

