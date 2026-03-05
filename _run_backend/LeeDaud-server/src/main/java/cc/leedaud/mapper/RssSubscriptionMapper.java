package cc.leedaud.mapper;

import cc.leedaud.dto.RssSubscriptionPageQueryDTO;
import cc.leedaud.entity.RssSubscriptions;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RssSubscriptionMapper {
    /**
     * 鎻掑叆RSS璁㈤槄
     * @param rssSubscriptions
     */
    void insert(RssSubscriptions rssSubscriptions);

    /**
     * 鍒嗛〉鏌ヨRSS璁㈤槄
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    Page<RssSubscriptions> pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO);

    /**
     * 鏇存柊RSS璁㈤槄
     * @param rssSubscriptions
     */
    void update(RssSubscriptions rssSubscriptions);

    /**
     * 鍒犻櫎RSS璁㈤槄
     * @param id
     */
    @Delete("delete from rss_subscriptions where id = #{id}")
    void deleteById(Long id);

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
    @Select("select * from rss_subscriptions where id = #{id}")
    RssSubscriptions getById(Long id);

    /**
     * 鑾峰彇鎵€鏈夋縺娲荤殑璁㈤槄
     * @return
     */
    @Select("select * from rss_subscriptions where is_active = 1 order by subscribe_time desc")
    List<RssSubscriptions> getAllActiveSubscriptions();

    /**
     * 鏍规嵁閭鏌ヨ璁㈤槄
     * @param email
     * @return
     */
    @Select("select * from rss_subscriptions where email = #{email}")
    RssSubscriptions getByEmail(String email);

    /**
     * 妫€鏌ヨ瀹㈡槸鍚︽湁婵€娲荤殑璁㈤槄
     * @param visitorId
     * @return
     */
    @Select("select count(*) > 0 from rss_subscriptions where visitor_id = #{visitorId} and is_active = 1")
    boolean hasActiveByVisitorId(Long visitorId);

    /**
     * 鏍规嵁璁垮ID鑾峰彇婵€娲荤殑璁㈤槄璁板綍
     * @param visitorId
     * @return
     */
    @Select("select * from rss_subscriptions where visitor_id = #{visitorId} and is_active = 1 limit 1")
    RssSubscriptions getActiveByVisitorId(Long visitorId);
}

