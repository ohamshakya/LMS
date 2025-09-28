package com.project.lms.admin.service;

import java.util.List;
import java.util.Map;

public interface DocumentService {
    List<Map<String, Object>> getAllDocumentsWithUrls();

    String getFileUrl(String fileName);
}
