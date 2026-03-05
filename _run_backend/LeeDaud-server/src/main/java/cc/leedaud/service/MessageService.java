package cc.leedaud.service;

import cc.leedaud.dto.MessageDTO;
import cc.leedaud.dto.MessagePageQueryDTO;
import cc.leedaud.dto.MessageReplyDTO;
import cc.leedaud.result.PageResult;
import cc.leedaud.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 鐣欒█鏈嶅姟
 */
public interface MessageService {

    /**
     * 璁垮鎻愪氦鐣欒█
     * @param messageDTO
     * @param request
     */
    void submitMessage(MessageDTO messageDTO, HttpServletRequest request);

    /**
     * 鍒嗛〉鏉′欢鏌ヨ鐣欒█
     * @param messagePageQueryDTO
     * @return
     */
    PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO);

    /**
     * 鎵归噺瀹℃牳閫氳繃鐣欒█
     * @param ids
     */
    void batchApprove(List<Long> ids);

    /**
     * 鎵归噺鍒犻櫎鐣欒█
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 绠＄悊鍛樺洖澶嶇暀瑷€
     * @param messageReplyDTO
     */
    void adminReply(MessageReplyDTO messageReplyDTO, HttpServletRequest request);

    // ===== 鍗氬绔柟娉?=====

    /**
     * 鑾峰彇宸插鏍哥暀瑷€鍒楄〃锛堟爲褰㈢粨鏋勶級
     */
    List<MessageVO> getMessageTree(Long visitorId);

    /**
     * 璁垮缂栬緫鐣欒█
     */
    void editMessage(cc.leedaud.dto.MessageEditDTO editDTO);

    /**
     * 璁垮鍒犻櫎鐣欒█
     */
    void visitorDeleteMessage(Long id, Long visitorId);
}


