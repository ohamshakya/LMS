package com.project.lms.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupResponse {
    private String pidx;
    private String status;
    private String transaction_id;
    private Integer amount;
    private Integer fee_amount;
    private String message;
}