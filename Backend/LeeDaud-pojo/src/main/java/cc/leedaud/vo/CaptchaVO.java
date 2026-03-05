package cc.leedaud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 楠岃瘉鐮乂O
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 楠岃瘉鐮両D */
    private String captchaId;

    /** 绠楁湳棰樼洰锛屽 "3 + 5 = ?" */
    private String question;

    /** 姝ｇ‘绛旀 */
    private int result;
}

