package cc.leedaud.service.impl;

import cc.leedaud.constant.StatusConstant;
import cc.leedaud.dto.VisitorPageQueryDTO;
import cc.leedaud.dto.VisitorRecordDTO;
import cc.leedaud.entity.Visitors;
import cc.leedaud.mapper.VisitorMapper;
import cc.leedaud.result.PageResult;
import cc.leedaud.service.AsyncVisitorService;
import cc.leedaud.service.BlockService;
import cc.leedaud.service.FingerprintService;
import cc.leedaud.service.VisitorService;
import cc.leedaud.utils.IpUtil;
import cc.leedaud.vo.VisitorRecordVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private AsyncVisitorService asyncVisitorService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FingerprintService fingerprintService;
    @Autowired
    private BlockService blockService;

    // Redis閿墠缂€
    public static final String VISITOR_KEY = "visitor:fingerprint:";

    /**
     * 璁板綍璁垮璁块棶淇℃伅
     * @param visitorRecordDTO
     * @param request
     * @return
     */
    public VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest request) {

        // 鐢熸垚/鑾峰彇浼氳瘽Id
        String sessionId = getOrCreateSessionId(request);

        // 鐢熸垚璁惧鎸囩汗
        String fingerprint = fingerprintService.generateVisitorFingerprint(visitorRecordDTO,request);

        // 鑾峰彇IP
        String ip = IpUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        // 妫€鏌ヨ瀹㈡槸鍚﹀湪缂撳瓨涓湁灏佺璁板綍
        blockService.checkIfBlocked(fingerprint);

        // 妫€鏌ヨ姹傞鐜?        blockService.checkRateLimit(fingerprint,ip);

        // 鏌ユ壘鎴栧垱寤鸿瀹㈣褰?        Visitors visitor = findOrCreateVisitor(fingerprint, sessionId, userAgent, ip);

        // 寮傛澶勭悊锛欼P鍦扮悊浣嶇疆鏌ヨ + 璁垮鍦扮悊淇℃伅鏇存柊 + 娴忚璁板綍鍐欏叆
        // 浼犻€?visitorId 鑰岄潪瀵硅薄寮曠敤锛岄伩鍏嶄富绾跨▼涓庡紓姝ョ嚎绋嬪叡浜彲鍙樺璞″鑷寸珵鎬佹潯浠?        asyncVisitorService.processGeoAndRecordViewAsync(
                visitor.getId(), ip, userAgent,
                visitorRecordDTO.getPagePath(),
                visitorRecordDTO.getReferer(),
                visitorRecordDTO.getPageTitle()
        );

        // 灏佽VO锛堢珛鍗宠繑鍥烇紝涓嶇瓑寰呭紓姝ユ搷浣滃畬鎴愶級
        VisitorRecordVO visitorRecordVO = VisitorRecordVO.builder()
                .visitorFingerprint(fingerprint)
                .sessionId(sessionId)
                .visitorId(visitor.getId())
                .isNewVisitor(visitor.getTotalViews() <= 1)
                .build();
        return visitorRecordVO;
    }

    /**
     * 鑾峰彇鎴栧垱寤轰細璇滻D
     * @param request
     * @return
     */
    private String getOrCreateSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session==null){
            session = request.getSession(true);
            // 璁剧疆浼氳瘽灞炴€у垱寤烘椂闂?            session.setAttribute("visitTime", LocalDateTime.now());
        }
        return session.getId();
    }


    /**
     * 鏌ユ壘鎴栧垱寤鸿瀹㈣褰曪紙涓嶅惈鍦扮悊浣嶇疆锛屽湴鐞嗕綅缃敱寮傛鏈嶅姟濉厖锛?     * @param fingerprint
     * @param sessionId
     * @param userAgent
     * @param ip
     * @return
     */
    private Visitors findOrCreateVisitor(String fingerprint, String sessionId,
                                         String userAgent, String ip){
        // 灏濊瘯浠嶳edis涓幏鍙栬瀹俊鎭?        String cacheKey = VISITOR_KEY + fingerprint;
        Visitors visitor = (Visitors) redisTemplate.opsForValue().get(cacheKey);

        if(visitor!=null){
            // 缂撳瓨鍛戒腑,鏇存柊鍩烘湰淇℃伅
            log.info("銆愯瀹㈣拷韪€戠紦瀛樺懡涓? id={}, fingerprint={}, ip={}, cachedViews={}",
                    visitor.getId(), fingerprint, ip, visitor.getTotalViews());
            visitor.setSessionId(sessionId);
            visitor.setIp(ip);
            visitor.setLastVisitTime(LocalDateTime.now());
            visitor.setTotalViews(visitor.getTotalViews() + 1);
            visitorMapper.updateById(visitor);
            // 鍥炲啓Redis缂撳瓨锛屼繚鎸佺紦瀛樻暟鎹笌鏁版嵁搴撳悓姝ワ紙淇totalViews绛夊瓧娈典笉涓€鑷寸殑闂锛?            redisTemplate.opsForValue().set(cacheKey, visitor, 1, TimeUnit.HOURS);
            return visitor;
        }

        // 缂撳瓨鏈懡涓紝閫氳繃鎸囩汗鏌ユ壘璁垮
        visitor = visitorMapper.findVisitorByFingerprint(fingerprint);

        if(visitor==null){
            // 鏂拌瀹細鍒涘缓璁板綍锛堝湴鐞嗕綅缃瓧娈电敱寮傛浠诲姟濉厖锛?            log.info("銆愯瀹㈣拷韪€戞柊璁垮鍒涘缓: fingerprint={}, ip={}", fingerprint, ip);
            visitor = Visitors.builder()
                    .fingerprint(fingerprint)
                    .sessionId(sessionId)
                    .ip(ip)
                    .userAgent(userAgent)
                    .firstVisitTime(LocalDateTime.now())
                    .lastVisitTime(LocalDateTime.now())
                    .totalViews(1L)
                    .isBlocked(StatusConstant.DISABLE)
                    .build();
            try {
                visitorMapper.insertVisitor(visitor);
                log.info("銆愯瀹㈣拷韪€戞柊璁垮鎻掑叆鎴愬姛: id={}, fingerprint={}", visitor.getId(), fingerprint);
            } catch (DuplicateKeyException e) {
                // 骞跺彂鍦烘櫙锛氬彟涓€涓姹傚凡缁忔彃鍏ヤ簡鐩稿悓鎸囩汗鐨勮瀹紝鍥為€€鍒版暟鎹簱鏌ヨ
                log.warn("銆愯瀹㈣拷韪€戝苟鍙戝垱寤猴紝鍥為€€鏌ヨ: fingerprint={}", fingerprint);
                visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
                if (visitor != null) {
                    visitor.setLastVisitTime(LocalDateTime.now());
                    visitor.setTotalViews(visitor.getTotalViews() + 1);
                    visitor.setSessionId(sessionId);
                    visitor.setIp(ip);
                    visitorMapper.updateById(visitor);
                }
            }
        }else{
            // 鑰佽瀹細鏇存柊鍩烘湰淇℃伅
            log.info("銆愯瀹㈣拷韪€戣€佽瀹㈡洿鏂? id={}, fingerprint={}, ip={}, dbViews={}",
                    visitor.getId(), fingerprint, ip, visitor.getTotalViews());
            visitor.setLastVisitTime(LocalDateTime.now());
            visitor.setTotalViews(visitor.getTotalViews() + 1);

            // 濡傛灉session宸茶繃鏈熸垨涓嶅悓锛屽垯瑙嗕负鏂扮殑娴忚鍣ㄤ細璇?            boolean sessionExpired = !sessionId.equals(visitor.getSessionId());
            if(sessionExpired){
                visitor.setSessionId(sessionId);
            }

            visitor.setIp(ip);
            visitorMapper.updateById(visitor);
        }

        // 缁熶竴鍐欏叆/鏇存柊Redis缂撳瓨
        if (visitor != null) {
            redisTemplate.opsForValue().set(cacheKey, visitor, 1, TimeUnit.HOURS);
        }
        return visitor;
    }

    /**
     * 鍒嗛〉鏌ヨ璁垮鍒楄〃
     * @param visitorPageQueryDTO
     * @return
     */
    public PageResult pageQuery(VisitorPageQueryDTO visitorPageQueryDTO) {
        PageHelper.startPage(visitorPageQueryDTO.getPage(), visitorPageQueryDTO.getPageSize());

        Page<Visitors> page = visitorMapper.pageQuery(visitorPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 鎵归噺灏佺璁垮
     * @param ids
     */
    public void batchBlock(List<Long> ids) {
        visitorMapper.batchBlock(ids);
    }

    /**
     * 鎵归噺瑙ｅ皝璁垮锛堝悓鏃舵竻闄?Redis 灏佺缂撳瓨锛?     * @param ids
     */
    public void batchUnblock(List<Long> ids) {
        // 鍏堟煡鍑鸿繖浜涜瀹㈢殑 fingerprint锛岀敤浜庢竻闄?Redis 灏佺缂撳瓨
        for (Long id : ids) {
            Visitors visitor = visitorMapper.findById(id);
            if (visitor != null && visitor.getFingerprint() != null) {
                String blockedKey = "visitor:blocked:" + visitor.getFingerprint();
                redisTemplate.delete(blockedKey);
                // 鍚屾椂娓呴櫎璁垮淇℃伅缂撳瓨锛岄伩鍏嶆棫鐨?isBlocked=1 鐘舵€佹畫鐣?                String visitorKey = VISITOR_KEY + visitor.getFingerprint();
                redisTemplate.delete(visitorKey);
            }
        }
        visitorMapper.batchUnblock(ids);
    }
}

