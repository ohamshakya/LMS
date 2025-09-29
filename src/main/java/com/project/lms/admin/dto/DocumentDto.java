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
    private String url;
    private MultipartFile document;
    private String documentType;
    private String fileName;
    private LocalDateTime createdAt;
}
