package com.project.lms.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiateResponse {
    private String pidx;
    private String payment_url;
    private String expires_at;
    private Integer expires_in;
    private String message;
}
