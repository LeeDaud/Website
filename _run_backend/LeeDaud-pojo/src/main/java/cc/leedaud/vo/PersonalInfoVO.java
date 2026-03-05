package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfoVO implements Serializable {

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
}

