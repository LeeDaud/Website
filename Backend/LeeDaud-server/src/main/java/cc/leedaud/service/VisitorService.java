package cc.leedaud.service;

import cc.leedaud.dto.VisitorPageQueryDTO;
import cc.leedaud.dto.VisitorRecordDTO;
import cc.leedaud.result.PageResult;
import cc.leedaud.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface VisitorService {
    /**
     * 璁板綍璁垮璁块棶淇℃伅
     * @param visitorRecordDTO
     * @param httpRequest
     * @return
     */
    VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest httpRequest);

    /**
     * 鍒嗛〉鏌ヨ璁垮鍒楄〃
     * @param visitorPageQueryDTO
     * @return
     */
    PageResult pageQuery(VisitorPageQueryDTO visitorPageQueryDTO);

    /**
     * 鎵归噺灏佺璁垮
     * @param ids
     */
    void batchBlock(List<Long> ids);

    /**
     * 鎵归噺瑙ｅ皝璁垮
     * @param ids
     */
    void batchUnblock(List<Long> ids);
}

