package com.project.lms.admin.service;


import com.project.lms.admin.dto.LookupResponse;
import com.project.lms.admin.dto.PaymentCallbackResponse;
import com.project.lms.admin.dto.PaymentInitiateResponse;
import com.project.lms.admin.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(Integer borrowId);

    LookupResponse verifyPayment(String pidx);

    void handleCallback(PaymentCallbackResponse callbackResponse);

    Payment getPaymentById(Integer id);

    Page<Payment> getAllPayments(String filter, Pageable pageable);

    Page<Payment> getUserPayments(Integer userId, String filter, Pageable pageable);
}