package cc.leedaud;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class LeeDaudBackendApplicationTests {

    @Test
    public void testPassword() throws Exception {
        String password = "123456"; // 鏇挎崲涓轰綘闇€瑕佺殑瀵嗙爜
        String salt = "123456";    //  鏇挎崲涓轰綘闇€瑕佺殑鐩愬€硷紝鍙互鏄换鎰忓瓧绗︿覆

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String combined = password + salt;
        byte[] hash = md.digest(combined.getBytes(StandardCharsets.UTF_8));

        // 杞崲涓哄崄鍏繘鍒?        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        System.out.println(hexString.toString());
    }

}

