package cc.leedaud.service;

import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    /**
     * 鏂囦欢涓婁紶
     * @param file 鏂囦欢
     */
    String uploadFile(MultipartFile file);
}

