package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 鍙嬫儏閾炬帴DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendLinkDTO implements Serializable {

    private Long id;

    // 缃戠珯鍚嶇О
    @NotBlank(message = "缃戠珯鍚嶇О涓嶈兘涓虹┖")
    @Size(max = 20, message = "缃戠珯鍚嶇О涓嶈兘瓒呰繃20瀛?)
    private String name;

    // 缃戠珯鍦板潃
    @NotBlank(message = "缃戠珯鍦板潃涓嶈兘涓虹┖")
    @Size(max = 100, message = "缃戠珯鍦板潃涓嶈兘瓒呰繃100瀛?)
    private String url;

    // 澶村儚url
    private String avatarUrl;

    // 缃戠珯鎻忚堪
    @Size(max = 255, message = "缃戠珯鎻忚堪涓嶈兘瓒呰繃255瀛?)
    private String description;

    // 鎺掑簭锛岃秺灏忚秺闈犲墠
    private Integer sort;

    // 鏄惁鍙
    private Integer isVisible;
}

