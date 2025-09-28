package com.project.lms.admin.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DocumentResponseRequest {
    private Integer id;
    private String documentType;
    private String fileUrl;    // URL to access the file
    private String fileName;
}
