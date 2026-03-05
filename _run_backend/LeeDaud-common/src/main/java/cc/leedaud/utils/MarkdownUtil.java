package cc.leedaud.utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Markdown宸ュ叿绫? */
public class MarkdownUtil {

    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * Markdown杞琀TML锛堝甫XSS闃叉姢锛?     * @param markdown Markdown鏂囨湰
     * @return 瀹夊叏鐨凥TML鏂囨湰
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        // 瑙ｆ瀽Markdown
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        
        // 瀹夊叏杩囨护锛堥槻姝SS锛?        return sanitizeHtml(html);
    }

    /**
     * 鍒ゆ柇鏄惁鏄?HTML 鍐呭锛堝瀵屾枃鏈紪杈戝櫒杈撳嚭锛?     */
    public static boolean isHtml(String content) {
        if (content == null) return false;
        String trimmed = content.trim();
        return trimmed.startsWith("<");
    }

    /**
     * 瀵?HTML 鍐呭杩涜 XSS 瀹夊叏杩囨护锛堜笉杩涜 Markdown 杞崲锛?     */
    public static String sanitize(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        return sanitizeHtml(html);
    }

    /**
     * 瀹夊叏鐨?HTML 杩囨护
     */
    private static String sanitizeHtml(String html) {
        Safelist safelist = Safelist.relaxed()
                .addTags("code", "pre", "hr")  // 鍏佽鐨勬爣绛?                .addAttributes("code", "class")  // 鍏佽code鏍囩鐨刢lass灞炴€э紙鐢ㄤ簬璇硶楂樹寒锛?                .addProtocols("a", "href", "http", "https", "mailto")  // 鍙厑璁稿畨鍏ㄥ崗璁?                .addAttributes("a", "target", "rel")  // 鍏佽target鍜宺el灞炴€?                .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");  // 鑷姩鍔犲畨鍏ㄥ睘鎬?        
        // 娓呯悊HTML
        return Jsoup.clean(html, safelist);
    }
}

