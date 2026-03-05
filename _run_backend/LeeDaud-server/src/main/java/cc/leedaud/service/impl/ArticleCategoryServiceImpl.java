package cc.leedaud.service.impl;

import cc.leedaud.dto.ArticleCategoryDTO;
import cc.leedaud.entity.ArticleCategories;
import cc.leedaud.mapper.ArticleCategoryMapper;
import cc.leedaud.mapper.ArticleMapper;
import cc.leedaud.service.ArticleCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 鑾峰彇鎵€鏈夋枃绔犲垎绫?     * @return
     */
    @Cacheable(value = "articleCategories", key = "'all'")
    public List<ArticleCategories> listAll() {
        return articleCategoryMapper.listAll();
    }

    /**
     * 娣诲姞鏂囩珷鍒嗙被
     * @param articleCategoryDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void addCategory(ArticleCategoryDTO articleCategoryDTO) {
        ArticleCategories articleCategories = new ArticleCategories();
        BeanUtils.copyProperties(articleCategoryDTO, articleCategories);
        articleCategoryMapper.insert(articleCategories);
    }

    /**
     * 鏇存柊鏂囩珷鍒嗙被锛堝惈鎺掑簭锛?     * @param articleCategoryDTO
     */
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateCategory(ArticleCategoryDTO articleCategoryDTO) {
        ArticleCategories articleCategories = new ArticleCategories();
        BeanUtils.copyProperties(articleCategoryDTO, articleCategories);
        articleCategoryMapper.update(articleCategories);
    }

    /**
     * 鎵归噺鍒犻櫎鏂囩珷鍒嗙被
     * @param ids
     */
    @Caching(evict = {
            @CacheEvict(value = "articleCategories", allEntries = true),
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void batchDelete(List<Long> ids) {
        // 妫€鏌ュ垎绫讳笅鏄惁鏈夊叧鑱旀枃绔?        for (Long id : ids) {
            Integer count = articleMapper.countByCategoryId(id);
            if (count != null && count > 0) {
                throw new RuntimeException("鍒嗙被涓嬪瓨鍦ㄥ叧鑱旀枃绔狅紝鏃犳硶鍒犻櫎");
            }
        }
        articleCategoryMapper.batchDelete(ids);
    }

    // ===== 鍗氬绔柟娉?=====

    @Cacheable(value = "articleCategories", key = "'visible'")
    public List<ArticleCategories> getVisibleCategories() {
        return articleCategoryMapper.getVisibleCategories();
    }
}

