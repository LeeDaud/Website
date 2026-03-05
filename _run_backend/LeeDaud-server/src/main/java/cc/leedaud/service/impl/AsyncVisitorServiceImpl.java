package cc.leedaud.service.impl;

import cc.leedaud.entity.Views;
import cc.leedaud.entity.Visitors;
import cc.leedaud.mapper.ViewMapper;
import cc.leedaud.mapper.VisitorMapper;
import cc.leedaud.service.AsyncVisitorService;
import cc.leedaud.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 寮傛璁垮鏈嶅姟瀹炵幇锛堝湴鐞嗕綅缃煡璇㈠拰娴忚璁板綍鍐欏叆寮傛鍖栵級
 */
@Service
@Slf4j
public class AsyncVisitorServiceImpl implements AsyncVisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ViewMapper viewMapper;

    /**
     * 寮傛澶勭悊锛欼P鍦扮悊浣嶇疆鏌ヨ + 璁垮鍦扮悊淇℃伅鏇存柊 + 娴忚璁板綍鍐欏叆
     * 鎺ユ敹 visitorId 鑰岄潪 Visitors 瀵硅薄寮曠敤锛岄伩鍏嶄富绾跨▼涓庡紓姝ョ嚎绋嬪叡浜彲鍙樺璞″鑷寸珵鎬佹潯浠?     */
    @Async("taskExecutor")
    public void processGeoAndRecordViewAsync(Long visitorId, String ip, String userAgent,
                                              String pagePath, String referer, String pageTitle) {
        try {
            // 鑰楁椂鎿嶄綔锛欼P鍦扮悊浣嶇疆鏌ヨ
            Map<String, String> geoInfo = IpUtil.getGeoInfo(ip);

            String country = geoInfo.get("country");
            String province = geoInfo.get("province");
            String city = geoInfo.get("city");

            // 浠呭湪鍦扮悊浣嶇疆鏈夋晥鏃舵洿鏂?            if (country != null && !country.isEmpty()) {
                // 浠庢暟鎹簱閲嶆柊璇诲彇璁垮璁板綍锛岀‘淇濇嬁鍒版渶鏂版暟鎹?                Visitors current = visitorMapper.findById(visitorId);
                if (current != null) {
                    boolean geoChanged = !equalsNullSafe(current.getCountry(), country)
                            || !equalsNullSafe(current.getProvince(), province)
                            || !equalsNullSafe(current.getCity(), city);

                    if (geoChanged) {
                        // 浠呮洿鏂板湴鐞嗕綅缃瓧娈碉紝閬垮厤涓庝富绾跨▼鐨勮闂鏁颁骇鐢熺珵鎬?                        Visitors geoUpdate = Visitors.builder()
                                .id(visitorId)
                                .country(country)
                                .province(province)
                                .city(city)
                                .longitude(geoInfo.get("longitude"))
                                .latitude(geoInfo.get("latitude"))
                                .build();
                        visitorMapper.updateById(geoUpdate);
                    }
                }
            }

            // 鍐欏叆娴忚璁板綍
            Views view = Views.builder()
                    .visitorId(visitorId)
                    .pagePath(pagePath)
                    .referer(referer)
                    .pageTitle(pageTitle)
                    .ipAddress(ip)
                    .userAgent(userAgent)
                    .viewTime(LocalDateTime.now())
                    .build();
            viewMapper.insert(view);

            log.debug("寮傛澶勭悊璁垮璁板綍瀹屾垚: visitorId={}, ip={}", visitorId, ip);
        } catch (Exception e) {
            log.error("寮傛澶勭悊璁垮璁板綍澶辫触: visitorId={}, ip={}, ex={}", visitorId, ip, e.getMessage());
        }
    }

    private boolean equalsNullSafe(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}

