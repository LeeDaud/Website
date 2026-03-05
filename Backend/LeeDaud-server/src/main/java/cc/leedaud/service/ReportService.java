package cc.leedaud.service;

import cc.leedaud.vo.*;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 鑾峰彇鍗氬缁熻鏁版嵁
     */
    BlogReportVO getBlogReport();

    /**
     * 娴忚閲忕粺璁?     */
    ViewReportVO getViewStatistics(LocalDate begin, LocalDate end);

    /**
     * 璁垮缁熻
     */
    VisitorReportVO getVisitorStatistics(LocalDate begin, LocalDate end);

    /**
     * 璁垮鐪佷唤鍒嗗竷缁熻
     */
    ProvinceVisitorVO getProvinceDistribution();

    /**
     * 鏂囩珷璁块棶閲忔帓琛屽墠鍗?     */
    ArticleViewTop10VO getArticleViewTop10();

    /**
     * 鑾峰彇绠＄悊绔€昏鏁版嵁
     */
    AdminOverviewVO getAdminOverview();
}

