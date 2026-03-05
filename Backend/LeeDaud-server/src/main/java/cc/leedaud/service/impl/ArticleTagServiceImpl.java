package cc.leedaud.service.impl;

import cc.leedaud.dto.ArticleTagDTO;
import cc.leedaud.entity.ArticleTags;
import cc.leedaud.mapper.ArticleTagMapper;
import cc.leedaud.service.ArticleTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    /**
     * 鑾峰彇鎵€鏈夋爣绛?     * @return
     */
    @Cacheable(value = "articleTags", key = "'all'")
    public List<ArticleTags> listAll() {
        List<ArticleTags> list = articleTagMapper.listAll();
        return list != null ? list : Collections.emptyList();
    }

    /**
     * 娣诲姞鏍囩
     * @param articleTagDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void addTag(ArticleTagDTO articleTagDTO) {
        ArticleTags articleTag = new ArticleTags();
        BeanUtils.copyProperties(articleTagDTO, articleTag);
        articleTagMapper.insert(articleTag);
    }

    /**
     * 淇敼鏍囩
     * @param articleTagDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateTag(ArticleTagDTO articleTagDTO) {
        ArticleTags articleTag = new ArticleTags();
        BeanUtils.copyProperties(articleTagDTO, articleTag);
        articleTagMapper.update(articleTag);
    }

    /**
     * 鎵归噺鍒犻櫎鏍囩
     * @param ids
     */
    @Caching(evict = {
            @CacheEvict(value = "articleTags", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    @Transactional
    public void batchDelete(List<Long> ids) {
        // 鍏堝垹闄ゅ叧鑱斿叧绯讳腑娑夊強杩欎簺鏍囩鐨勮褰?        articleTagMapper.batchDelete(ids);
    }

    /**
     * 鑾峰彇鏍囩
     * @return
     */
    @Cacheable(value = "articleTags", key = "'visible'")
    public List<ArticleTags> getVisibleTags() {
        List<ArticleTags> list = articleTagMapper.getVisibleTags();
        return list != null ? list : Collections.emptyList();
    }
}

