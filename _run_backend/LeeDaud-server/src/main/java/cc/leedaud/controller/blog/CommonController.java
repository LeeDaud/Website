package cc.leedaud.controller.blog;

import cc.leedaud.result.Result;
import cc.leedaud.vo.CaptchaVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 鍗氬绔叕鍏辨帴鍙? */
@RestController("blogCommonController")
@RequestMapping("/blog/common")
public class CommonController {

    private final Random random = new Random();

    /**
     * 鐢熸垚绠楁湳楠岃瘉鐮?     */
    @GetMapping("/captcha/generate")
    public Result<CaptchaVO> generateCaptcha() {
        int num1 = random.nextInt(9) + 1;
        int num2 = random.nextInt(9) + 1;

        String[] operators = {"+", "-", "脳"};
        String operator = operators[random.nextInt(operators.length)];

        // 纭繚鍑忔硶涓嶄細浜х敓璐熸暟
        if ("-".equals(operator) && num1 < num2) {
            int temp = num1; num1 = num2; num2 = temp;
        }

        int result;
        switch (operator) {
            case "+": result = num1 + num2; break;
            case "-": result = num1 - num2; break;
            case "脳": result = num1 * num2; break;
            default: result = num1 + num2;
        }

        String question = num1 + " " + operator + " " + num2 + " = ?";
        String captchaId = "captcha_" + System.currentTimeMillis() + "_" + random.nextInt(1000);

        CaptchaVO captchaVO = CaptchaVO.builder()
                .captchaId(captchaId)
                .question(question)
                .result(result)
                .build();

        return Result.success(captchaVO);
    }
}

