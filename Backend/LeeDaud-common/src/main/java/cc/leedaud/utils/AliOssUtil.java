package cc.leedaud.utils;

import cc.leedaud.constant.MessageConstant;
import cc.leedaud.exception.UploadFileErrorException;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Locale;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String upload(byte[] bytes, String extension, String fileName) {
        if (!isConfigured()) {
            throw new UploadFileErrorException("File upload service is not configured");
        }

        String normalizedExtension = extension.toLowerCase(Locale.ROOT);
        String objectName = getFileCategory(normalizedExtension) + "/" + fileName;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS upload failed: code={}, requestId={}, hostId={}",
                    oe.getErrorCode(), oe.getRequestId(), oe.getHostId(), oe);
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        } catch (ClientException ce) {
            log.error("OSS client upload failed: {}", ce.getMessage(), ce);
            throw new UploadFileErrorException(MessageConstant.UPLOAD_FAILED);
        } finally {
            ossClient.shutdown();
        }

        return new StringBuilder("https://")
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName)
                .toString();
    }

    public boolean isConfigured() {
        return StringUtils.hasText(endpoint)
                && StringUtils.hasText(accessKeyId)
                && StringUtils.hasText(accessKeySecret)
                && StringUtils.hasText(bucketName);
    }

    public String getFileCategory(String extension) {
        switch (extension.toLowerCase(Locale.ROOT)) {
            case "jpg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
            case "jpeg":
            case "svg":
            case "ico":
            case "tiff":
                return "image";
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
            case "wmv":
            case "flv":
            case "webm":
            case "m4v":
            case "3gp":
                return "video";
            case "mp3":
            case "wav":
            case "wma":
            case "ogg":
            case "aac":
            case "flac":
            case "m4a":
            case "ape":
            case "mid":
            case "midi":
                return "audio";
            case "lrc":
            case "lrcx":
            case "krc":
            case "qrc":
            case "trc":
            case "ksc":
                return "lyric";
            case "txt":
            case "md":
            case "rtf":
                return "text";
            case "pdf":
                return "pdf";
            case "doc":
            case "docx":
            case "dot":
            case "dotx":
                return "word";
            case "xls":
            case "xlsx":
            case "xlt":
            case "xltx":
                return "excel";
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
            case "bz2":
                return "archive";
            case "ttf":
            case "otf":
            case "woff":
            case "woff2":
            case "eot":
                return "font";
            default:
                return "other";
        }
    }
}
