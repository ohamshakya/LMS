package com.project.lms.admin.controller;

import com.project.lms.admin.service.DocumentService;
import com.project.lms.common.util.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/document")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseWrapper<List<Map<String, Object>>> getAllDocuments() {
        List<Map<String, Object>> documents = documentService.getAllDocumentsWithUrls();
        return new ResponseWrapper<>(documents, "retrieved successfully", HttpStatus.OK.value(), true);
    }

    @GetMapping("/{id}")
    public ResponseWrapper<Map<String, Object>> getDocumentById(@PathVariable Integer id) {
        Map<String, Object> document = documentService.getDocumentById(id);
        return new ResponseWrapper<>(document, "Document retrieved successfully", HttpStatus.OK.value(), true);
    }
}
