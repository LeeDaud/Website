package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangePasswordDTO implements Serializable {

    @NotBlank(message = "鏃у瘑鐮佷笉鑳戒负绌?)
    private String oldPassword;

    @NotBlank(message = "鏂板瘑鐮佷笉鑳戒负绌?)
    @Size(min = 6, max = 32, message = "瀵嗙爜闀垮害搴斿湪6-32浣嶄箣闂?)
    private String newPassword;

    @NotBlank(message = "纭瀵嗙爜涓嶈兘涓虹┖")
    private String confirmNewPassword;
}

