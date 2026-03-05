package cc.leedaud.controller.blog;

import cc.leedaud.result.Result;
import cc.leedaud.service.ReportService;
import cc.leedaud.vo.BlogReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鍗氬绔粺璁＄浉鍏虫帴鍙? */
@Slf4j
@RestController("blogReportController")
@RequestMapping("/blog/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 鑾峰彇鍗氬缁熻鏁版嵁
     */
    @GetMapping
    public Result<BlogReportVO> getBlogReport() {
        log.info("鍗氬绔幏鍙栫粺璁℃暟鎹?);
        BlogReportVO blogReportVO = reportService.getBlogReport();
        return Result.success(blogReportVO);
    }
}

