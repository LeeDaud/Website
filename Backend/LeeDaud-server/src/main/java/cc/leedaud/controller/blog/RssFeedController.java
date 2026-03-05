package cc.leedaud.controller.blog;

import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.result.Result;
import cc.leedaud.service.ArticleService;
import cc.leedaud.service.PersonalInfoService;
import cc.leedaud.service.RssFeedService;
import cc.leedaud.vo.BlogArticleVO;
import cc.leedaud.vo.PersonalInfoVO;
import cc.leedaud.result.PageResult;
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
 * 鍗氬绔?RSS Feed 鎺ュ彛
 */
@Slf4j
@RestController("blogRssFeedController")
@RequestMapping("/blog")
public class RssFeedController {

    @Autowired
    private RssFeedService rssFeedService;

    /**
     * 鐢熸垚 RSS 2.0 Feed XML
     */
    @GetMapping(value = "/rss", produces = "application/xml; charset=UTF-8")
    @Cacheable(value = "rssFeed", key = "'xml'")
    public String rssFeed() {
        String xml = rssFeedService.generateRssFeed();
        return xml;
    }
}

