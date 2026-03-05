package cc.leedaud.service.impl;

import cc.leedaud.properties.WebsiteProperties;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.ArticleService;
import cc.leedaud.service.SitemapService;
import cc.leedaud.vo.BlogArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SitemapServiceImpl implements SitemapService {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private WebsiteProperties websiteProperties;

    private static final DateTimeFormatter W3C_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 鐢熸垚Sitemap XML
     * @return
     */
    public String generateSitemap() {
        String BLOG_BASE_URL = websiteProperties.getBlog();

        // 鑾峰彇鎵€鏈夊凡鍙戝竷鏂囩珷锛堝彇瓒冲澶氾紝鏈€澶?00鏉★級
        PageResult pageResult = articleService.getPublishedPage(1, 500);
        @SuppressWarnings("unchecked")
        List<BlogArticleVO> articles = (List<BlogArticleVO>) pageResult.getRecords();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // 棣栭〉
        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("</loc>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("    <priority>1.0</priority>\n");
        xml.append("  </url>\n");

        // 鏂囩珷椤甸潰
        if (articles != null) {
            for (BlogArticleVO article : articles) {
                xml.append("  <url>\n");
                xml.append("    <loc>").append(BLOG_BASE_URL).append("/article/").append(article.getSlug()).append("</loc>\n");
                if (article.getPublishTime() != null) {
                    xml.append("    <lastmod>").append(article.getPublishTime().format(W3C_DATE_FMT)).append("</lastmod>\n");
                }
                xml.append("    <changefreq>weekly</changefreq>\n");
                xml.append("    <priority>0.8</priority>\n");
                xml.append("  </url>\n");
            }
        }

        // 褰掓。椤?        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("/archive</loc>\n");
        xml.append("    <changefreq>weekly</changefreq>\n");
        xml.append("    <priority>0.6</priority>\n");
        xml.append("  </url>\n");

        // 鍙嬮摼椤?        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("/friends</loc>\n");
        xml.append("    <changefreq>monthly</changefreq>\n");
        xml.append("    <priority>0.5</priority>\n");
        xml.append("  </url>\n");

        // 鐣欒█鏉?        xml.append("  <url>\n");
        xml.append("    <loc>").append(BLOG_BASE_URL).append("/message</loc>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("    <priority>0.5</priority>\n");
        xml.append("  </url>\n");

        xml.append("</urlset>\n");

        return xml.toString();
    }
}

