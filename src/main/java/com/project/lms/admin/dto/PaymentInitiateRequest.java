package com.project.lms.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiateRequest {
    private String purchase_order_id;
    private String purchase_order_name;
    private Integer amount;
    private String return_url;
    private String website_url;
    private CustomerInfo customer_info;
}