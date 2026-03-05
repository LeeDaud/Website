package cc.leedaud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 涓汉淇℃伅
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 鏄电О
    private String nickname;

    // 鏍囩
    private String tag;

    // 涓汉绠€浠?    private String description;

    // 澶村儚url
    private String avatar;

    // 涓汉缃戠珯
    private String website;

    // 鐢靛瓙閭
    private String email;

    // GitHub
    private String github;

    // 鎵€鍦ㄥ湴
    private String location;

    // 鍒涘缓鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 鏇存柊鏃堕棿
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

