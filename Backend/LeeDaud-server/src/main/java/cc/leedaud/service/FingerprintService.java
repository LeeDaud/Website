package cc.leedaud.service;

import cc.leedaud.dto.VisitorRecordDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 璁垮鎸囩汗鏈嶅姟
 */
public interface FingerprintService {
    
    /**
     * 鐢熸垚璁垮鎸囩汗
     * @param dto
     * @param request
     * @return
     */
    String generateVisitorFingerprint(VisitorRecordDTO dto, HttpServletRequest request);
}

