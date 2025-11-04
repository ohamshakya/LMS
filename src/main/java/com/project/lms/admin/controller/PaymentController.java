package com.project.lms.admin.controller;

import com.project.lms.admin.dto.LookupResponse;
import com.project.lms.admin.dto.PaymentCallbackResponse;
import com.project.lms.admin.dto.PaymentInitiateResponse;
import com.project.lms.admin.service.PaymentService;
import com.project.lms.common.util.PaginationUtil;
import com.project.lms.common.util.ResponseWrapper;
import com.project.lms.payment.entity.Payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<ResponseWrapper<PaymentInitiateResponse>> initiatePayment(@RequestParam Integer borrowId) {
        PaymentInitiateResponse response = paymentService.initiatePayment(borrowId);
        return ResponseEntity.ok(new ResponseWrapper<>(
                response,
                "Payment initiated successfully",
                200,
                true
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseWrapper<LookupResponse>> verifyPayment(@RequestParam String pidx) {
        LookupResponse response = paymentService.verifyPayment(pidx);
        return ResponseEntity.ok(new ResponseWrapper<>(
                response,
                "Payment verified successfully",
                200,
                true
        ));
    }

    @GetMapping("/khalti/callback")
    public ResponseEntity<ResponseWrapper<PaymentCallbackResponse>> handleCallback(
            @RequestParam String pidx) {

        var lookup = paymentService.verifyPayment(pidx);

        PaymentCallbackResponse callback = new PaymentCallbackResponse();
        callback.setPidx(pidx);
        callback.setTxnId(lookup.getTransaction_id());
        callback.setStatus(lookup.getStatus());

        paymentService.handleCallback(callback);

        return ResponseEntity.ok(new ResponseWrapper<>(
                callback,
                "Payment callback processed successfully",
                200,
                true
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Payment>> getPaymentById(@PathVariable Integer id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ResponseWrapper<>(
                payment,
                "Payment fetched successfully",
                200,
                true
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper<Page<Payment>>> getAllPayments(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Optional<Integer> page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder
    ) {
        Pageable pageable = PaginationUtil.preparePaginationUtil(page, size, sortBy, sortOrder);
        Page<Payment> payments = paymentService.getAllPayments(filter, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(
                payments,
                "Payments fetched successfully",
                200,
                true
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseWrapper<Page<Payment>>> getUserPayments(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            @RequestParam(required = false) Optional<Integer> page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder
    ) {
        Pageable pageable = PaginationUtil.preparePaginationUtil(page, size, sortBy, sortOrder);
        Page<Payment> payments = paymentService.getUserPayments(userId, filter, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(
                payments,
                "User payments fetched successfully",
                200,
                true
        ));
    }
}