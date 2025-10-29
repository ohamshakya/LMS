package com.project.lms.admin.service.impl;

import com.project.lms.admin.entity.Document;
import com.project.lms.admin.repository.DocumentRepo;
import com.project.lms.admin.service.DocumentService;
import com.project.lms.common.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepo documentRepo;

    public DocumentServiceImpl(DocumentRepo documentRepo) {
        this.documentRepo = documentRepo;
    }

    public List<Map<String, Object>> getAllDocumentsWithUrls() {
        // Your existing code to get documents from database
        List<Document> documents = documentRepo.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Document doc : documents) {
            Map<String, Object> docMap = new HashMap<>();
            docMap.put("id", doc.getId());
            docMap.put("url", "http://localhost:8080/pics/" + doc.getDoc()); // ← ADD URL HERE
            docMap.put("documentType", doc.getDocumentType());
            docMap.put("fileName", doc.getDoc());
            docMap.put("createdAt", doc.getCreatedAt());

            result.add(docMap);
        }

        return result;
    }

    // Your existing method
    public String getFileUrl(String fileName) {
        return "http://localhost:8080/pics/" + fileName;
    }

    @Override
    public Map<String, Object> getDocumentById(Integer id) {
        Document doc = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", doc.getId());
        docMap.put("url", "http://localhost:8080/pics/" + doc.getDoc());
        docMap.put("documentType", doc.getDocumentType());
        docMap.put("fileName", doc.getDoc());
        docMap.put("createdAt", doc.getCreatedAt());

        // You can also include book information if needed
        if (doc.getBook() != null) {
            Map<String, Object> bookInfo = new HashMap<>();
            bookInfo.put("id", doc.getBook().getId());
            bookInfo.put("title", doc.getBook().getTitle());
            // Add other book fields as needed
            docMap.put("book", bookInfo);
        }

        return docMap;
    }

}
