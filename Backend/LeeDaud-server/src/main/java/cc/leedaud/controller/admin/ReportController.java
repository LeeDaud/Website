package cc.leedaud.controller.admin;

import cc.leedaud.result.Result;
import cc.leedaud.service.ReportService;
import cc.leedaud.vo.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 绠＄悊绔粺璁＄浉鍏虫帴鍙? */
@Slf4j
@RestController("adminReportController")
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 娴忚閲忕粺璁?     */
    @GetMapping("/viewStatistics")
    public Result<ViewReportVO> getViewStatistics(
            @NotNull(message = "寮€濮嬫棩鏈熶笉鑳戒负绌?) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @NotNull(message = "缁撴潫鏃ユ湡涓嶈兘涓虹┖") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("娴忚閲忕粺璁? {} - {}", begin, end);
        ViewReportVO viewReportVO = reportService.getViewStatistics(begin, end);
        return Result.success(viewReportVO);
    }

    /**
     * 璁垮缁熻
     */
    @GetMapping("/visitorStatistics")
    public Result<VisitorReportVO> getVisitorStatistics(
            @NotNull(message = "寮€濮嬫棩鏈熶笉鑳戒负绌?) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @NotNull(message = "缁撴潫鏃ユ湡涓嶈兘涓虹┖") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("璁垮缁熻: {} - {}", begin, end);
        VisitorReportVO visitorReportVO = reportService.getVisitorStatistics(begin, end);
        return Result.success(visitorReportVO);
    }

    /**
     * 璁垮鐪佷唤鍒嗗竷缁熻
     */
    @GetMapping("/provinceDistribution")
    public Result<ProvinceVisitorVO> getProvinceDistribution() {
        log.info("璁垮鐪佷唤鍒嗗竷缁熻");
        ProvinceVisitorVO provinceVisitorVO = reportService.getProvinceDistribution();
        return Result.success(provinceVisitorVO);
    }

    /**
     * 鏂囩珷璁块棶閲忔帓琛屽墠鍗?     */
    @GetMapping("/articleViewTop10")
    public Result<ArticleViewTop10VO> getArticleViewTop10() {
        log.info("鏂囩珷璁块棶閲忔帓琛屽墠鍗?);
        ArticleViewTop10VO articleViewTop10VO = reportService.getArticleViewTop10();
        return Result.success(articleViewTop10VO);
    }

    /**
     * 鑾峰彇鎬昏鏁版嵁锛堟€昏闂噺銆佹€昏瀹級
     */
    @GetMapping("/overview")
    public Result<AdminOverviewVO> getAdminOverview() {
        log.info("鑾峰彇绠＄悊绔€昏鏁版嵁");
        AdminOverviewVO adminOverviewVO = reportService.getAdminOverview();
        return Result.success(adminOverviewVO);
    }
}

