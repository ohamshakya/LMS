package com.project.lms.admin.controller;

import com.project.lms.admin.dto.ReservationDto;
import com.project.lms.admin.service.ReservationService;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@Slf4j
@Tag(name = "RESERVATION", description = "RESERVATION API FOR LMS")
@CrossOrigin("*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/book/{bookId}/member/{memberId}")
    public ResponseWrapper<ReservationDto> placeReservation(@PathVariable Integer bookId, @PathVariable Integer memberId) {
        log.info("inside place reservation : controller");
        ReservationDto response = reservationService.placeReservation(bookId, memberId);
        return new ResponseWrapper<>(response, "placed successfully", HttpStatus.OK.value());
    }

    @GetMapping
    public ResponseWrapper<List<ReservationDto>> getAllReservation() {
        log.info("inside get all reservation : controller");
        List<ReservationDto> allReservations = reservationService.getAllReservations();
        return new ResponseWrapper<>(allReservations, "retrieved successfully", HttpStatus.OK.value());
    }

    @GetMapping("/member/{memberId}")
    public ResponseWrapper<List<ReservationDto>> getReservationByMember(@PathVariable Integer memberId) {
        log.info("inside get reservation by member : controller");
        List<ReservationDto> reservationsByMember = reservationService.getReservationsByMember(memberId);
        return new ResponseWrapper<>(reservationsByMember, "retrieved by member", HttpStatus.OK.value());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseWrapper<String> cancelReservation(@PathVariable Integer reservationId) {
        log.info("inside cancel reservation : controller");
        reservationService.cancelReservation(reservationId);
        return new ResponseWrapper<>("cancelled", "canceled", HttpStatus.OK.value());
    }
}
