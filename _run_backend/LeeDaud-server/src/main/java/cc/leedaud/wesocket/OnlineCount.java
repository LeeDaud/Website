package cc.leedaud.wesocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 鍦ㄧ嚎浜烘暟缁熻
 */
@Slf4j
@Component
@ServerEndpoint("/ws/online")
public class OnlineCount {

    // 瀛樻斁鎵€鏈夎繛鎺ョ殑浼氳瘽
    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    // 鍦ㄧ嚎浜烘暟璁℃暟鍣?    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 杩炴帴寤虹珛
     */
    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        int count = onlineCount.incrementAndGet();

        log.info("鏂拌繛鎺? {}, 褰撳墠鍦ㄧ嚎: {} 浜?, session.getId(), count);

        // 鍙戦€佸綋鍓嶅湪绾夸汉鏁扮粰鏂扮敤鎴?        sendMessage(session, String.valueOf(count));

        // 骞挎挱鏇存柊缁欐墍鏈夌敤鎴?        broadcastCount();
    }

    /**
     * 鏀跺埌娑堟伅
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 濡傛灉鏄績璺虫秷鎭紝鍥炲纭
        if ("ping".equals(message)) {
            sendMessage(session, "pong");
        }
    }

    /**
     * 杩炴帴鍏抽棴
     */
    @OnClose
    public void onClose(Session session) {
        // 鍙湁褰?session 纭疄鍦?map 涓椂鎵嶉€掑噺锛岄槻姝笌 @OnError 閲嶅鎵ｅ噺瀵艰嚧璐熸暟
        if (sessions.remove(session.getId()) != null) {
            int count = onlineCount.decrementAndGet();
            log.info("杩炴帴鍏抽棴: {}, 褰撳墠鍦ㄧ嚎: {} 浜?, session.getId(), count);
            broadcastCount();
        }
    }

    /**
     * 杩炴帴鍑洪敊
     */
    @OnError
    public void onError(Session session, Throwable error) {
        if (sessions.remove(session.getId()) != null) {
            int count = onlineCount.decrementAndGet();
            log.debug("WebSocket 杩炴帴寮傚父: {}, 褰撳墠鍦ㄧ嚎: {} 浜?, session.getId(), count);
            broadcastCount();
        }
    }

    /**
     * 鍙戦€佹秷鎭粰鍗曚釜鐢ㄦ埛锛堜娇鐢?getAsyncRemote 閬垮厤骞跺彂鍐欏啿绐侊級
     */
    private void sendMessage(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        } catch (Exception e) {
            log.debug("鍙戦€佹秷鎭け璐? {}", session.getId());
        }
    }

    /**
     * 骞挎挱鍦ㄧ嚎浜烘暟缁欐墍鏈夌敤鎴?     */
    private void broadcastCount() {
        String message = String.valueOf(onlineCount.get());

        sessions.forEach((id, session) -> {
            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                log.debug("骞挎挱娑堟伅澶辫触: {}", id);
            }
        });
    }
}

