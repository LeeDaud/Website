package cc.leedaud.service;

import cc.leedaud.exception.UploadFileErrorException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Optional;

@Service
public class LocalUploadStorageService {

    private static final String PUBLIC_PREFIX = "/api/common/file/";

    private final Path uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads")
            .toAbsolutePath()
            .normalize();

    public String store(byte[] bytes, String category, String fileName) {
        String safeCategory = sanitizeCategory(category);
        String safeFileName = sanitizeFileName(fileName);
        Path target = buildPath(safeCategory, safeFileName);

        try {
            Files.createDirectories(target.getParent());
            Files.write(target, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new UploadFileErrorException("Failed to store uploaded file locally");
        }

        return PUBLIC_PREFIX + safeCategory + "/" + safeFileName;
    }

    public Optional<Path> resolvePublicPath(String publicPath) {
        if (!StringUtils.hasText(publicPath)) {
            return Optional.empty();
        }

        String path = publicPath.trim();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            try {
                path = java.net.URI.create(path).getPath();
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        }

        if (!path.startsWith(PUBLIC_PREFIX)) {
            return Optional.empty();
        }

        String relativePath = path.substring(PUBLIC_PREFIX.length());
        String[] segments = relativePath.split("/", 2);
        if (segments.length != 2) {
            return Optional.empty();
        }

        try {
            return Optional.of(buildPath(segments[0], segments[1]));
        } catch (UploadFileErrorException ex) {
            return Optional.empty();
        }
    }

    public Optional<Path> resolve(String category, String fileName) {
        try {
            Path path = buildPath(category, fileName);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return Optional.empty();
            }
            return Optional.of(path);
        } catch (UploadFileErrorException ex) {
            return Optional.empty();
        }
    }

    private Path buildPath(String category, String fileName) {
        String safeCategory = sanitizeCategory(category);
        String safeFileName = sanitizeFileName(fileName);
        Path resolved = uploadRoot.resolve(safeCategory).resolve(safeFileName).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new UploadFileErrorException("Invalid local upload path");
        }
        return resolved;
    }

    private String sanitizeCategory(String category) {
        String value = (category == null ? "" : category.trim().toLowerCase(Locale.ROOT));
        if (!value.matches("[a-z0-9_-]+")) {
            throw new UploadFileErrorException("Invalid upload category");
        }
        return value;
    }

    private String sanitizeFileName(String fileName) {
        Path fileNamePath = Paths.get(fileName == null ? "" : fileName).getFileName();
        String value = fileNamePath == null ? "" : fileNamePath.toString().trim();
        if (!StringUtils.hasText(value) || value.contains("..")) {
            throw new UploadFileErrorException("Invalid upload file name");
        }
        return value;
    }
}
