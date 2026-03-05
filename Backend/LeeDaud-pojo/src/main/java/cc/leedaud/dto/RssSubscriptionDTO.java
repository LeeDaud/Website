package cc.leedaud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RSS璁㈤槄DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RssSubscriptionDTO {

    // 璁垮ID
    @NotNull(message = "璁垮ID涓嶈兘涓虹┖")
    private Long visitorId;

    // 鏄电О
    @Size(max = 15, message = "鏄电О涓嶈兘瓒呰繃15瀛?)
    private String nickname;

    // 閭
    @NotBlank(message = "閭涓嶈兘涓虹┖")
    @Email(message = "閭鏍煎紡涓嶆纭?)
    private String email;
}

