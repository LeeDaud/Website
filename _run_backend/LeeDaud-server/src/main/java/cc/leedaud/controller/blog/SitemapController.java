package cc.leedaud.controller.blog;

import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.ArticleService;
import cc.leedaud.service.SitemapService;
import cc.leedaud.vo.BlogArticleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 鍗氬绔?Sitemap 鎺ュ彛
 */
@Slf4j
@RestController("blogSitemapController")
@RequestMapping("/blog")
public class SitemapController {

    @Autowired
    private SitemapService sitemapService;


    /**
     * 鍔ㄦ€佺敓鎴愮珯鐐瑰湴鍥?XML
     */
    @GetMapping(value = "/sitemap.xml", produces = "application/xml; charset=UTF-8")
    @Cacheable(value = "sitemap", key = "'xml'")
    public String sitemap() {
        String xml = sitemapService.generateSitemap();
        return xml;
    }
}

