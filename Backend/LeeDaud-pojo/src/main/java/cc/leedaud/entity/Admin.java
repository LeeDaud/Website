package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 绠＄悊鍛? */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鐢ㄦ埛鍚?    private String username;

    // 鍔犲瘑鍚庣殑瀵嗙爜
    private String password;

    // 鐩愬€?    private String salt;

    // 鏄电О
    private String nickname;

    // 鐢靛瓙閭
    private String email;

    // 瑙掕壊 1-绠＄悊鍛?0-娓稿
    private Integer role;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

