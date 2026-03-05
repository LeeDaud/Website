package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RSS璁㈤槄鐘舵€乂O锛堣繑鍥炵粰鍗氬鍓嶇锛? */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RssSubscriptionStatusVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 鏄惁宸茶闃?*/
    private boolean subscribed;

    /** 璁㈤槄鏃朵娇鐢ㄧ殑鏄电О */
    private String nickname;

    /** 璁㈤槄鏃朵娇鐢ㄧ殑閭 */
    private String email;
}

