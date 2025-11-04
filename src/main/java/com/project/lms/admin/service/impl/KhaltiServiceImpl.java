package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.*;
import com.project.lms.admin.entity.Users;

import com.project.lms.admin.service.KhaltiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class KhaltiServiceImpl implements KhaltiService {

    private final RestTemplate restTemplate;

    @Value("${khalti.base.url}")
    private String baseUrl;

    @Value("${khalti.secret.key}")
    private String secretKey;

    @Value("${khalti.return.url}")
    private String returnUrl;

    @Value("${khalti.website.url}")
    private String websiteUrl;

    public KhaltiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentInitiateResponse initiatePayment(String borrowId, String borrowName, int amount, Users user) {
        try {
            log.info("Initiating Khalti payment for borrow ID: {}, amount: {}", borrowId, amount);

            PaymentInitiateRequest request = new PaymentInitiateRequest();
            request.setPurchase_order_id(borrowId);
            request.setPurchase_order_name(borrowName);

            // Convert amount to paisa
            int amountInPaisa = amount * 100;
            request.setAmount(amountInPaisa);

            request.setReturn_url(returnUrl);
            request.setWebsite_url(websiteUrl);

            CustomerInfo customerInfo = new CustomerInfo(
                    user.getFirstName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "") + user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber()
            );
            request.setCustomer_info(customerInfo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", secretKey); // Make sure this includes "key "

            log.debug("Khalti Request Headers - Authorization: {}", secretKey);
            log.debug("Khalti Request Body: {}", request);

            HttpEntity<PaymentInitiateRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<PaymentInitiateResponse> response = restTemplate.exchange(
                    baseUrl + "/epayment/initiate/",
                    HttpMethod.POST,
                    entity,
                    PaymentInitiateResponse.class
            );

            log.info("Khalti payment initiated successfully with pidx: {}",
                    response.getBody() != null ? response.getBody().getPidx() : "null");

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to initiate Khalti payment. URL: {}, Error: {}",
                    baseUrl + "/epayment/initiate/", e.getMessage());
            throw new RuntimeException("Failed to initiate payment with Khalti: " + e.getMessage());
        }
    }

    @Override
    public LookupResponse lookupPayment(String pidx) {
        try {
            log.info("Looking up Khalti payment with pidx: {}", pidx);

            LookupRequest request = new LookupRequest(pidx);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", secretKey);

            HttpEntity<LookupRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<LookupResponse> response = restTemplate.exchange(
                    baseUrl + "/epayment/lookup/",
                    HttpMethod.POST,
                    entity,
                    LookupResponse.class
            );

            log.info("Khalti payment lookup completed for pidx: {}", pidx);
            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to lookup Khalti payment with pidx: {}. Error: {}", pidx, e.getMessage());
            throw new RuntimeException("Failed to lookup payment with Khalti: " + e.getMessage());
        }
    }
}