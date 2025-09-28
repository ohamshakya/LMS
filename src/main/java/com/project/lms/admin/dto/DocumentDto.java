package com.project.lms.admin.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {
    private Integer id;
    private MultipartFile document;
    private String documentType;
    private String storedFileName;
    private LocalDateTime createdAt;
}
