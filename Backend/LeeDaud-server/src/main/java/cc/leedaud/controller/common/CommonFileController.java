package cc.leedaud.controller.common;

import cc.leedaud.service.LocalUploadStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/common/file")
public class CommonFileController {

    @Autowired
    private LocalUploadStorageService localUploadStorageService;

    @GetMapping("/{category}/{fileName:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String category,
            @PathVariable String fileName) throws IOException {
        Optional<Path> filePath = localUploadStorageService.resolve(category, fileName);
        if (filePath.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path path = filePath.get();
        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (contentType != null) {
            try {
                mediaType = MediaType.parseMediaType(contentType);
            } catch (IllegalArgumentException ignored) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(resource);
    }
}
