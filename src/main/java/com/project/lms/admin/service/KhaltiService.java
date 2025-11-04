package com.project.lms.admin.service;

import com.project.lms.admin.dto.LookupResponse;
import com.project.lms.admin.dto.PaymentInitiateResponse;
import com.project.lms.admin.entity.Users;

public interface KhaltiService {
    PaymentInitiateResponse initiatePayment(String borrowId, String borrowName, int amount, Users user);
    LookupResponse lookupPayment(String pidx);
}