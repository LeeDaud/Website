package cc.leedaud.service.impl;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.exception.UploadFileErrorException;
import cc.leedaud.properties.ImageProperties;
import cc.leedaud.service.CommonService;
import cc.leedaud.service.LocalUploadStorageService;
import cc.leedaud.utils.AliOssUtil;
import cc.leedaud.utils.ImageCompressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private ImageCompressUtil imageCompressUtil;
    @Autowired
    private ImageProperties imageProperties;
    @Autowired
    private LocalUploadStorageService localUploadStorageService;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UploadFileErrorException(MessageConstant.FILE_EMPTY);
        }

        try {
            String extension = extractExtension(file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            String category = aliOssUtil.getFileCategory(extension);

            if ("image".equals(category)) {
                bytes = imageCompressUtil.compress(file);
                extension = imageProperties.getOutPutFormat().toLowerCase(Locale.ROOT);
                category = aliOssUtil.getFileCategory(extension);
            }

            String uuidFileName = UUID.randomUUID() + "." + extension;
            if (aliOssUtil.isConfigured()) {
                return aliOssUtil.upload(bytes, extension, uuidFileName);
            }
            return localUploadStorageService.store(bytes, category, uuidFileName);
        } catch (IOException ex) {
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        }
    }

    private String extractExtension(String fileName) {
        String value = fileName == null ? "" : fileName.trim();
        int dotIndex = value.lastIndexOf('.');
        if (!StringUtils.hasText(value) || dotIndex < 0 || dotIndex == value.length() - 1) {
            throw new UploadFileErrorException("Unsupported file type");
        }
        return value.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
