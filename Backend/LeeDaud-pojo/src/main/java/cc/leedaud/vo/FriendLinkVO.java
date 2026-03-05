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
public class FriendLinkVO implements Serializable {
    private Long id;

    // 缃戠珯鍚嶇О
    private String name;

    // 缃戠珯鍦板潃
    private String url;

    // 澶村儚url
    private String avatarUrl;

    // 缃戠珯鎻忚堪
    private String description;

    // 鎺掑簭
    private Integer sort;
}

