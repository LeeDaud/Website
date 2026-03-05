package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 涓汉淇℃伅DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO implements Serializable {

    private Long id;

    // 鏄电О
    @NotBlank(message = "鏄电О涓嶈兘涓虹┖")
    @Size(max = 20, message = "鏄电О涓嶈兘瓒呰繃20瀛?)
    private String nickname;

    // 鏍囩
    @NotBlank(message = "鏍囩涓嶈兘涓虹┖")
    @Size(max = 30, message = "鏍囩涓嶈兘瓒呰繃30瀛?)
    private String tag;

    // 涓汉绠€浠?    @Size(max = 50, message = "涓汉绠€浠嬩笉鑳借秴杩?0瀛?)
    private String description;

    // 澶村儚url
    private String avatar;

    // 涓汉缃戠珯
    @Size(max = 100, message = "缃戠珯鍦板潃涓嶈兘瓒呰繃100瀛?)
    private String website;

    // 鐢靛瓙閭
    @Size(max = 50, message = "閭涓嶈兘瓒呰繃50瀛?)
    private String email;

    // GitHub
    @Size(max = 100, message = "GitHub鍦板潃涓嶈兘瓒呰繃100瀛?)
    private String github;

    // 鎵€鍦ㄥ湴
    @Size(max = 50, message = "鎵€鍦ㄥ湴涓嶈兘瓒呰繃50瀛?)
    private String location;
}

