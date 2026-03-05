package cc.leedaud.service;

import cc.leedaud.dto.ViewPageQueryDTO;
import cc.leedaud.result.PageResult;

import java.util.List;

public interface ViewService {
    /**
     * 鍒嗛〉鏌ヨ娴忚璁板綍
     * @param viewPageQueryDTO
     * @return
     */
    PageResult pageQuery(ViewPageQueryDTO viewPageQueryDTO);

    /**
     * 鎵归噺鍒犻櫎娴忚璁板綍
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

