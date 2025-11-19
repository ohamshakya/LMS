package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.LookupResponse;
import com.project.lms.admin.dto.PaymentCallbackResponse;
import com.project.lms.admin.dto.PaymentInitiateResponse;
import com.project.lms.admin.entity.Borrow;
import com.project.lms.admin.entity.Users;
import com.project.lms.admin.repository.BorrowRepo;
import com.project.lms.admin.repository.PaymentRepository;
import com.project.lms.admin.repository.UsersRepo;
import com.project.lms.admin.service.KhaltiService;
import com.project.lms.admin.service.PaymentService;
import com.project.lms.common.enums.PaymentMethod;
import com.project.lms.common.enums.PaymentStatus;
import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.admin.entity.Payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BorrowRepo borrowRepository;
    private final UsersRepo usersRepository;
    private final KhaltiService khaltiService;

    @Transactional
    @Override
    public PaymentInitiateResponse initiatePayment(Integer borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id: " + borrowId));

        // Check if fine amount exists and is greater than 0
        if (borrow.getFineAmount() == null || borrow.getFineAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("No fine amount to pay for this borrow record");
        }

        // Check if payment already exists for this borrow
        Optional<Payment> existingPayment = paymentRepository.findByBorrowId(borrowId);

        if (existingPayment.isPresent()) {
            com.project.lms.admin.entity.Payment payment = existingPayment.get();

            // If payment is already completed, throw an exception
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                throw new RuntimeException("Payment already completed for this borrow record");
            }

            // If payment is pending, reuse the existing payment record
            PaymentInitiateResponse response = khaltiService.initiatePayment(
                    String.valueOf(borrow.getId()),
                    "Fine-Borrow-" + borrow.getId(),
                    borrow.getFineAmount().intValue(),
                    borrow.getUser()
            );

            // Update the existing payment with new transaction details
            payment.setTransactionId(response.getPidx());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            return response;
        }

        // Create new payment if none exists
        PaymentInitiateResponse response = khaltiService.initiatePayment(
                String.valueOf(borrow.getId()),
                "Fine-Borrow-" + borrow.getId(),
                borrow.getFineAmount().intValue(),
                borrow.getUser()
        );

        Payment payment = Payment.builder()
                .borrow(borrow)
                .method(PaymentMethod.KHALTI)
                .status(PaymentStatus.PENDING)
                .amount(borrow.getFineAmount())
                .transactionId(response.getPidx())
                .build();

        paymentRepository.save(payment);

        return response;
    }

    @Override
    public LookupResponse verifyPayment(String pidx) {
        LookupResponse lookupResponse = khaltiService.lookupPayment(pidx);

        Payment payment = paymentRepository.findByTransactionId(pidx)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction: " + pidx));

        if ("Completed".equalsIgnoreCase(lookupResponse.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            // Update borrow record to mark fine as paid
            Borrow borrow = payment.getBorrow();
            borrow.setFineAmount(BigDecimal.ZERO); // Set fine amount to zero after payment
            borrowRepository.save(borrow);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);

        return lookupResponse;
    }

    @Override
    public void handleCallback(PaymentCallbackResponse callbackResponse) {
        verifyPayment(callbackResponse.getPidx());
    }

    @Override
    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    @Override
    public Page<Payment> getAllPayments(String filter, Pageable pageable) {
        LocalDateTime start = null;
        LocalDateTime now = LocalDateTime.now();

        switch (filter != null ? filter.toLowerCase() : "") {
            case "today":
                start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "lastweek":
                start = now.minusWeeks(1);
                break;
            case "15days":
                start = now.minusDays(15);
                break;
            case "lastmonth":
                start = now.minusMonths(1);
                break;
            case "lastyear":
                start = now.minusYears(1);
                break;
        }

        if (start != null) {
            return paymentRepository.findAllByCreatedAtAfter(start, pageable);
        } else {
            return paymentRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Payment> getUserPayments(Integer userId, String filter, Pageable pageable) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now();

        switch (filter != null ? filter.toUpperCase() : "ALL") {
            case "LAST WEEK":
                start = end.minusWeeks(1);
                break;
            case "LAST MONTH":
                start = end.minusMonths(1);
                break;
            case "LAST 15 DAYS":
                start = end.minusDays(15);
                break;
            case "LAST YEAR":
                start = end.minusYears(1);
                break;
            case "TODAY":
                start = end.withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "ALL":
            default:
                break;
        }

        if (start != null) {
            return paymentRepository.findByBorrowUserAndCreatedAtAfter(user, start, pageable);
        } else {
            return paymentRepository.findByBorrowUser(user, pageable);
        }
    }

    // Add this method to handle Khalti service exceptions
    private PaymentInitiateResponse handleKhaltiPaymentInitiation(Integer borrowId, Borrow borrow) {
        try {
            return khaltiService.initiatePayment(
                    String.valueOf(borrow.getId()),
                    "Fine-Borrow-" + borrow.getId(),
                    borrow.getFineAmount().intValue(),
                    borrow.getUser()
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("401")) {
                throw new RuntimeException("Khalti authentication failed. Please check your secret key configuration.");
            } else if (e.getMessage().contains("400")) {
                throw new RuntimeException("Invalid request to Khalti. Please check the request parameters.");
            } else {
                throw new RuntimeException("Payment service temporarily unavailable. Please try again later.");
            }
        }
    }
}