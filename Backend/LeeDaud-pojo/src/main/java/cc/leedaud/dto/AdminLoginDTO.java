package cc.leedaud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginDTO implements Serializable {

    @NotBlank(message = "鐢ㄦ埛鍚嶄笉鑳戒负绌?)
    private String username;

    @NotBlank(message = "瀵嗙爜涓嶈兘涓虹┖")
    private String password;

    @NotBlank(message = "楠岃瘉鐮佷笉鑳戒负绌?)
    private String code;
}

