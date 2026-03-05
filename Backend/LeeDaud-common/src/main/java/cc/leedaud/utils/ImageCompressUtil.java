package cc.leedaud.utils;

import cc.leedaud.properties.ImageProperties;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 鍥剧墖鍘嬬缉宸ュ叿绫? */
@Component
@Slf4j
public class ImageCompressUtil {

    @Autowired
    private ImageProperties imageProperties;

    // 鏀寔鐨勫浘鐗囨牸寮?    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif"
    );

    /**
     * 鍥剧墖鍘嬬缉
     * @param file
     * @return
     */
    public byte[] compress(MultipartFile file) throws IOException {
        // 濡傛灉涓嶉渶瑕佸帇缂╋紝鐩存帴杩斿洖鍘熸枃浠跺瓧鑺?        if(!shouldCompress(file)){
           return file.getBytes();
        }

        // 璁板綍鍘熸枃浠朵俊鎭?        long originalSize = file.getSize();
        String originalName = file.getOriginalFilename();

        log.info("寮€濮嬪帇缂? {} ({}KB)", originalName, originalSize / 1024);

        // 绗竴娆″帇缂?        byte[] compressedBytes = compressWithQuality(file.getBytes(), imageProperties.getQuality());

        // 濡傛灉杩樻槸澶ぇ锛岄€掑綊鍘嬬缉
        int attempts = 0;
        double currentQuality = imageProperties.getQuality();

        while (isOversized(compressedBytes) && attempts < 10) {
            // 閫愭闄嶄綆璐ㄩ噺
            currentQuality = Math.max(0.3, currentQuality - 0.05);
            compressedBytes = compressWithQuality(compressedBytes, currentQuality);
            attempts++;
        }

        // 璁板綍鍘嬬缉鍚庝俊鎭?        long compressedSize = compressedBytes.length;
        double ratio = 1.0 - (double) compressedSize / originalSize;

        log.info("鍘嬬缉瀹屾垚: {} ({}KB -> {}KB, 鍘嬬缉鐜? {}, 璐ㄩ噺: {})",
                originalName,
                originalSize / 1024,
                compressedSize / 1024,
                String.format("%.2f",ratio),
                String.format("%.2f", currentQuality));

        return compressedBytes;
    }

    /**
     * 浣跨敤鎸囧畾璐ㄩ噺鍘嬬缉鍥剧墖
     */
    private byte[] compressWithQuality(byte[] inputBytes, double quality) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .scale(1.0)  // 淇濇寔鍘熷昂瀵?                    .outputFormat(imageProperties.getOutPutFormat())
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    private boolean shouldCompress(MultipartFile file) throws IOException {
        // 妫€鏌ユ槸鍚﹀紑鍚浘鐗囧帇缂?        if(!imageProperties.isEnabled()){
            return false;
        }
        // 妫€鏌ユ枃浠剁被鍨?        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return false;
        }
        String extension = getFileExtension(originalName).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            return false;
        }

        // 妫€鏌ユ枃浠跺ぇ灏? 濡傛灉娌¤秴杩囬檺鍒讹紝涓嶅帇缂?        if(!isOversized(file.getBytes())){
            return false;
        }

        return true;
    }

    /**
     * 妫€鏌ユ槸鍚﹁秴杩囬檺鍒跺ぇ灏?     */
    private boolean isOversized(byte[] data) {
        int sizeKb = data.length / 1024;
        return sizeKb > imageProperties.getMaxSizeKb();
    }

    /**
     * 鑾峰彇鏂囦欢鎵╁睍鍚?     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}

