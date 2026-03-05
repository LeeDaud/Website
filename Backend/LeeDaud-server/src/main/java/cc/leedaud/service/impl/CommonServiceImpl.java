package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.exception.UploadFileErrorException;
import cc.leedaud.properties.ImageProperties;
import cc.leedaud.service.CommonService;
import cc.leedaud.utils.AliOssUtil;
import cc.leedaud.utils.ImageCompressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private ImageCompressUtil imageCompressUtil;
    @Autowired
    private ImageProperties imageProperties;

    /**
     * 鏂囦欢涓婁紶
     * @param file
     */
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new UploadFileErrorException(MessageConstant.FILE_EMPTY);
        }
        try {
            // 鑾峰彇鏂囦欢鍚?            String fileName = file.getOriginalFilename();
            // 鑾峰彇鏂囦欢鍚庣紑
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);
            // 鑾峰彇鏂囦欢瀛楄妭鏁扮粍
            byte[] bytes = file.getBytes();

            // 濡傛灉鏄浘鐗囷紝鍏堝帇缂╁啀涓婁紶
            if(aliOssUtil.getFileCategory(extension).equals("image")){
                bytes = imageCompressUtil.compress(file);
                extension = imageProperties.getOutPutFormat();
            }

            // 鑾峰彇uuid鏂囦欢鍚?            String uuidFileName = UUID.randomUUID()+"."+extension;
            // 涓婁紶鏂囦欢
            String fileUrl =aliOssUtil.upload(bytes,extension,uuidFileName);

            return fileUrl;

        } catch (IOException e) {
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        }
    }
}

