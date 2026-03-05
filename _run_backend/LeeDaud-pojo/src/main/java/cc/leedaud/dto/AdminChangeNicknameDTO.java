package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangeNicknameDTO {

    @NotBlank(message = "鏄电О涓嶈兘涓虹┖")
    @Size(max = 30, message = "鏄电О涓嶈兘瓒呰繃30瀛?)
    private String nickname;
}

