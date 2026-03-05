package cc.leedaud.service;

/**
 * 寮傛璁垮鏈嶅姟锛堝湴鐞嗕綅缃煡璇€佽瀹㈣褰曟洿鏂般€佹祻瑙堣褰曞啓鍏ワ級
 */
public interface AsyncVisitorService {

    /**
     * 寮傛澶勭悊璁垮鍦扮悊浣嶇疆鏇存柊鍜屾祻瑙堣褰曞啓鍏?     * @param visitorId 璁垮ID锛堥伩鍏嶄紶閫掑叡浜璞″紩鐢ㄥ鑷寸珵鎬佹潯浠讹級
     * @param ip 瀹㈡埛绔疘P
     * @param userAgent 鐢ㄦ埛浠ｇ悊
     * @param pagePath 椤甸潰璺緞
     * @param referer 鏉ユ簮URL
     * @param pageTitle 椤甸潰鏍囬
     */
    void processGeoAndRecordViewAsync(Long visitorId, String ip, String userAgent,
                                      String pagePath, String referer, String pageTitle);
}

