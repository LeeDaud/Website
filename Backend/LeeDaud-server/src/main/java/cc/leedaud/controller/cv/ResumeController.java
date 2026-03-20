package cc.leedaud.controller.cv;

import cc.leedaud.entity.SystemConfig;
import cc.leedaud.result.Result;
import cc.leedaud.service.SystemConfigService;
import cc.leedaud.vo.ResumeStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController("cvResumeController")
@RequestMapping("/cv/resume")
public class ResumeController {

    private static final String RESUME_PDF_CONFIG_KEY = "cv.resume_pdf_url";
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 15000;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/status")
    public Result<ResumeStatusVO> getResumeStatus() {
        return Result.success(
                ResumeStatusVO.builder()
                        .available(StringUtils.hasText(getResumePdfUrl()))
                        .build()
        );
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadResume() {
        String resumePdfUrl = getResumePdfUrl();
        if (!StringUtils.hasText(resumePdfUrl)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(resumePdfUrl).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                log.warn("Resume PDF fetch failed with status {} for {}", responseCode, resumePdfUrl);
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }

            byte[] fileBytes;
            try (InputStream inputStream = connection.getInputStream()) {
                fileBytes = StreamUtils.copyToByteArray(inputStream);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setCacheControl(CacheControl.noStore().getHeaderValue());
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("resume.pdf", StandardCharsets.UTF_8)
                            .build()
            );
            headers.setContentLength(fileBytes.length);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException | IOException ex) {
            log.error("Resume PDF download failed for {}", resumePdfUrl, ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getResumePdfUrl() {
        SystemConfig systemConfig = systemConfigService.getByKey(RESUME_PDF_CONFIG_KEY);
        if (systemConfig == null || !StringUtils.hasText(systemConfig.getConfigValue())) {
            return null;
        }
        return systemConfig.getConfigValue().trim();
    }
}
