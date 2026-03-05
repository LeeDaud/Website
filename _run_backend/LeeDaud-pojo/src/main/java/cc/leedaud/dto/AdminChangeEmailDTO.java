package cc.leedaud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangeEmailDTO {

    @NotBlank(message = "閭涓嶈兘涓虹┖")
    @Email(message = "閭鏍煎紡涓嶆纭?)
    private String email;

    @NotBlank(message = "楠岃瘉鐮佷笉鑳戒负绌?)
    private String code;
}

