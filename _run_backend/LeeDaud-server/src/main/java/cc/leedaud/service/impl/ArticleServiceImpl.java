package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.constant.StatusConstant;
import cc.leedaud.dto.ArticleDTO;
import cc.leedaud.dto.ArticlePageQueryDTO;
import cc.leedaud.entity.Articles;
import cc.leedaud.entity.ArticleTags;
import cc.leedaud.entity.RssSubscriptions;
import cc.leedaud.exception.ArticleException;
import cc.leedaud.mapper.ArticleMapper;
import cc.leedaud.mapper.ArticleTagMapper;
import cc.leedaud.mapper.RssSubscriptionMapper;
import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.ArticleService;
import cc.leedaud.service.AsyncEmailService;
import cc.leedaud.utils.MarkdownUtil;
import cc.leedaud.vo.ArticleArchiveItemVO;
import cc.leedaud.vo.ArticleArchiveVO;
import cc.leedaud.vo.ArticleVO;
import cc.leedaud.vo.BlogArticleDetailVO;
import cc.leedaud.vo.BlogArticleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 鏂囩珷鏈嶅姟瀹炵幇
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private RssSubscriptionMapper rssSubscriptionMapper;

    @Autowired
    private WebsiteProperties websiteProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_COUNT_KEY = "article:viewCount";

    /**
     * 鍒涘缓鏂囩珷
     * @param articleDTO
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void createArticle(ArticleDTO articleDTO) {
        Articles articles = new Articles();
        BeanUtils.copyProperties(articleDTO, articles);

        // 浼樺厛浣跨敤鍓嶇缂栬緫鍣ㄦ覆鏌撶殑HTML锛屽惁鍒欏悗绔浆鎹?        if (articleDTO.getContentHtml() != null && !articleDTO.getContentHtml().isBlank()) {
            articles.setContentHtml(articleDTO.getContentHtml());
        } else {
            String rawContent = articleDTO.getContentMarkdown();
            String contentHtml = MarkdownUtil.isHtml(rawContent)
                    ? MarkdownUtil.sanitize(rawContent)
                    : MarkdownUtil.toHtml(rawContent);
            articles.setContentHtml(contentHtml);
        }

        // 璁＄畻瀛楁暟鍜岄槄璇绘椂闂?        String plainText = articleDTO.getContentMarkdown();
        long wordCount = countWords(plainText);
        long readingTime = Math.max(1, wordCount / 300); // 鎸夋瘡鍒嗛挓300瀛椾及绠?        articles.setWordCount(wordCount);
        articles.setReadingTime(readingTime);

        // 璁剧疆鍙戝竷淇℃伅
        if (articleDTO.getIsPublished() != null && articleDTO.getIsPublished().equals(StatusConstant.ENABLE)) {
            articles.setPublishTime(LocalDateTime.now());
        }

        // 鍒濆鍖栫粺璁″瓧娈靛拰榛樿鐘舵€?        articles.setViewCount(0L);
        articles.setLikeCount(0L);
        articles.setCommentCount(0L);
        if (articles.getIsTop() == null) {
            articles.setIsTop(0);
        }

        articleMapper.insert(articles);

        // 淇濆瓨鏂囩珷-鏍囩鍏宠仈
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            articleTagMapper.batchInsertRelations(articles.getId(), articleDTO.getTagIds());
        }
    }

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鏂囩珷鍒楄〃锛堝惈鑽夌锛?     * @param articlePageQueryDTO
     * @return
     */
    public PageResult pageQuery(ArticlePageQueryDTO articlePageQueryDTO) {
        PageHelper.startPage(articlePageQueryDTO.getPage(), articlePageQueryDTO.getPageSize());
        Page<ArticleVO> page = articleMapper.pageQuery(articlePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 鏍规嵁ID鑾峰彇鏂囩珷璇︽儏
     * @param id
     * @return
     */
    public Articles getById(Long id) {
        Articles articles = articleMapper.getById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        // 濉厖鏍囩ID鍒楄〃锛岀敤浜庣鐞嗙缂栬緫鏃跺洖鏄?        List<Long> tagIds = articleTagMapper.getTagIdsByArticleId(id);
        articles.setTagIds(tagIds);
        return articles;
    }

    /**
     * 鏇存柊鏂囩珷
     * @param articleDTO
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateArticle(ArticleDTO articleDTO) {
        Articles articles = articleMapper.getById(articleDTO.getId());
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        BeanUtils.copyProperties(articleDTO, articles);

        // 濡傛灉浠庤崏绋垮垏鎹㈠埌鍙戝竷鐘舵€佷笖灏氭棤鍙戝竷鏃堕棿锛岃缃彂甯冩椂闂?        if (articleDTO.getIsPublished() != null
                && articleDTO.getIsPublished().equals(StatusConstant.ENABLE)
                && articles.getPublishTime() == null) {
            articles.setPublishTime(LocalDateTime.now());
        }

        // 濡傛灉Markdown鍐呭鏈夋洿鏂帮紝閲嶆柊鐢熸垚HTML骞惰绠楀瓧鏁?        if (articleDTO.getContentMarkdown() != null) {
            // 浼樺厛浣跨敤鍓嶇缂栬緫鍣ㄦ覆鏌撶殑HTML
            if (articleDTO.getContentHtml() != null && !articleDTO.getContentHtml().isBlank()) {
                articles.setContentHtml(articleDTO.getContentHtml());
            } else {
                String raw = articleDTO.getContentMarkdown();
                String contentHtml = MarkdownUtil.isHtml(raw)
                        ? MarkdownUtil.sanitize(raw)
                        : MarkdownUtil.toHtml(raw);
                articles.setContentHtml(contentHtml);
            }

            long wordCount = countWords(articleDTO.getContentMarkdown());
            long readingTime = Math.max(1, wordCount / 300);
            articles.setWordCount(wordCount);
            articles.setReadingTime(readingTime);
        }

        articleMapper.update(articles);

        // 鏇存柊鏂囩珷-鏍囩鍏宠仈
        if (articleDTO.getTagIds() != null) {
            articleTagMapper.deleteRelationsByArticleId(articleDTO.getId());
            if (!articleDTO.getTagIds().isEmpty()) {
                articleTagMapper.batchInsertRelations(articleDTO.getId(), articleDTO.getTagIds());
            }
        }
    }

    /**
     * 鎵归噺鍒犻櫎鏂囩珷
     * @param ids
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void batchDelete(List<Long> ids) {
        articleTagMapper.batchDeleteRelationsByArticleIds(ids);
        articleMapper.batchDelete(ids);
    }

    /**
     * 鍙戝竷/鍙栨秷鍙戝竷鏂囩珷
     * @param id
     * @param isPublished
     */
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void publishOrCancel(Long id, Integer isPublished) {
        Articles articles = articleMapper.getById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        Articles updateArticle = Articles.builder()
                .id(id)
                .isPublished(isPublished)
                .build();

        // 鍙戝竷鏃惰缃彂甯冩椂闂达紙浠呴娆″彂甯冭缃級
        if (isPublished.equals(StatusConstant.ENABLE) && articles.getPublishTime() == null) {
            updateArticle.setPublishTime(LocalDateTime.now());
        }

        articleMapper.update(updateArticle);

        // 鍙戝竷鏃堕€氱煡RSS璁㈤槄鑰?        if (isPublished.equals(StatusConstant.ENABLE)) {
            notifyRssSubscribers(articles);
        }
    }

    /**
     * 缃《/鍙栨秷缃《鏂囩珷
     * @param id
     * @param isTop
     */
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true)
    })
    public void toggleTop(Long id, Integer isTop) {
        Articles articles = articleMapper.getById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        Articles updateArticle = Articles.builder()
                .id(id)
                .isTop(isTop)
                .build();

        articleMapper.update(updateArticle);
    }

    /**
     * 閫氱煡RSS璁㈤槄鑰呮柊鏂囩珷鍙戝竷
     */
    private void notifyRssSubscribers(Articles article) {
        try {
            List<RssSubscriptions> subscribers = rssSubscriptionMapper.getAllActiveSubscriptions();
            if (subscribers == null || subscribers.isEmpty()) {
                return;
            }
            String articleUrl = websiteProperties.getBlog() + "/article/" + article.getSlug();
            for (RssSubscriptions subscriber : subscribers) {
                asyncEmailService.sendNewArticleNotificationAsync(
                        subscriber.getEmail(),
                        subscriber.getNickname() != null ? subscriber.getNickname() : "璁㈤槄鑰?,
                        article.getTitle(),
                        article.getSummary(),
                        articleUrl
                );
            }
            log.info("宸插悜 {} 涓猂SS璁㈤槄鑰呭彂閫佹柊鏂囩珷閫氱煡: title={}", subscribers.size(), article.getTitle());
        } catch (Exception e) {
            log.error("閫氱煡RSS璁㈤槄鑰呭紓甯? title={}, ex={}", article.getTitle(), e.getMessage());
        }
    }

    /**
     * 鏂囩珷鎼滅储锛堟爣棰樸€佸唴瀹癸級
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult search(String keyword, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<ArticleVO> pageResult = articleMapper.search(keyword);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    // ===== 鍗氬绔柟娉?=====

    @Cacheable(value = "articleList", key = "'page:' + #page + ':' + #pageSize")
    public PageResult getPublishedPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.getPublishedPage();
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Cacheable(value = "articleDetail", key = "#slug")
    public BlogArticleDetailVO getBySlug(String slug) {
        BlogArticleDetailVO articleDetail = articleMapper.getBySlug(slug);
        if (articleDetail == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        // 濉厖鏍囩鍚嶇О鍒楄〃
        List<ArticleTags> tags = articleTagMapper.getTagsByArticleId(articleDetail.getId());
        if (tags != null && !tags.isEmpty()) {
            articleDetail.setTagNames(tags.stream().map(ArticleTags::getName).toList());
        }

        // 濉厖涓婁竴绡?涓嬩竴绡囧鑸?        articleDetail.setPrevArticle(articleMapper.getPrevArticle(articleDetail.getId()));
        articleDetail.setNextArticle(articleMapper.getNextArticle(articleDetail.getId()));

        // 濉厖鐩稿叧鏂囩珷鎺ㄨ崘锛堝悓鍒嗙被锛屾帓闄ゅ綋鍓嶆枃绔狅紝鏈€澶?绡囷級
        if (articleDetail.getCategoryId() != null) {
            articleDetail.setRelatedArticles(
                    articleMapper.getRelatedArticles(articleDetail.getId(), articleDetail.getCategoryId()));
        }

        return articleDetail;
    }

    /**
     * 鏂囩珷娴忚閲?1锛堝啓鍏edis锛屽畾鏃跺悓姝ySQL锛?     */
    public void incrementViewCount(Long articleId) {
        redisTemplate.opsForHash().increment(VIEW_COUNT_KEY, articleId.toString(), 1);
    }

    @Cacheable(value = "articleList", key = "'cat:' + #categoryId + ':' + #page + ':' + #pageSize")
    public PageResult getPublishedByCategoryId(Long categoryId, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.getPublishedByCategoryId(categoryId);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Cacheable(value = "articleArchive", key = "'all'")
    public List<ArticleArchiveVO> getArchive() {
        List<ArticleArchiveItemVO> allArticles = articleMapper.getArchiveList();
        // 鎸夊勾鏈堝垎缁勶紙鍒╃敤鏁版嵁搴撶殑 publish_year, publish_month 鐢熸垚鍒楋級
        Map<String, ArticleArchiveVO> archiveMap = new LinkedHashMap<>();
        for (ArticleArchiveItemVO item : allArticles) {
            if (item.getPublishTime() == null) {
                continue;
            }
            int year = item.getPublishTime().getYear();
            int month = item.getPublishTime().getMonthValue();
            String key = year + "-" + month;
            ArticleArchiveVO archiveVO = archiveMap.computeIfAbsent(key, k ->
                    ArticleArchiveVO.builder()
                            .year(year)
                            .month(month)
                            .articles(new ArrayList<>())
                            .build()
            );
            archiveVO.getArticles().add(item);
        }
        return new ArrayList<>(archiveMap.values());
    }

    @Cacheable(value = "articleList", key = "'search:' + #keyword + ':' + #page + ':' + #pageSize")
    public PageResult searchPublished(String keyword, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.searchPublished(keyword);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Cacheable(value = "articleList", key = "'tag:' + #tagId + ':' + #page + ':' + #pageSize")
    public PageResult getPublishedByTagId(Long tagId, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.getPublishedByTagId(tagId);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    /**
     * 缁熻瀛楁暟锛堜腑鏂囩畻1瀛楋紝鑻辨枃鍗曡瘝绠?瀛楋級
     * @param text
     * @return
     */
    private long countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 鍘婚櫎Markdown璇硶绗﹀彿
        String cleanText = text.replaceAll("[#*`>\\-\\[\\]()!|]", "");
        // 涓枃瀛楃鏁?        long chineseCount = cleanText.chars()
                .filter(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                .count();
        // 鑻辨枃鍗曡瘝鏁?        String englishText = cleanText.replaceAll("[\\u4e00-\\u9fff]", " ");
        String[] words = englishText.trim().split("\\s+");
        long englishCount = 0;
        for (String word : words) {
            if (!word.isEmpty() && word.matches(".*[a-zA-Z0-9].*")) {
                englishCount++;
            }
        }
        return chineseCount + englishCount;
    }
}

