package com.project.lms.common.util;

import com.project.lms.common.exception.FileStorageException;
import com.project.lms.common.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class FileUploadUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    public static final String UPLOAD_DIR = "pics";

    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Files cannot be empty");
        }
        validateFileSizeAndType(file);
    }

    public static void validateFileSizeAndType(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size cannot exceed 5MB");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new FileValidationException("Invalid file type. Allowed types are: " +
                        String.join(", ", ALLOWED_EXTENSIONS));
            }
        }
    }

    public static void validateFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            validateFileSizeAndType(file);
        }
    }

    public static String storeFile(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Generate a unique filename
            String fileName = StringUtils.cleanPath(
                    UUID.randomUUID() + "-" + file.getOriginalFilename()
            );

            // Copy file to the target location
            Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file. Please try again!");
        }
    }
}
