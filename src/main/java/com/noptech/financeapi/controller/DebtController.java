package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.*;
import com.noptech.financeapi.service.DebtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

    @GetMapping(value = "/debts", produces = "application/json")
    public ResponseEntity<?> getAllDebts(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        log.info("[DebtController] - Fetching all debts for userId: {}", userId);

        return ResponseEntity
                .ok()
                .body("Not implemented yet");
    }


    @GetMapping(value = "/debts/{debtId}", produces = "application/json")
    public ResponseEntity<DebtResponseDto> getDebtById(HttpServletRequest request, @PathVariable String debtId) {
        var userId = request.getAttribute("userId");
        log.info("[DebtController] - Fetching debts for userId {} and debtId: {}", userId, debtId);

        var debt = debtService.getDebtsById(userId.toString(), Long.valueOf(debtId));

        return ResponseEntity
                .ok().body(DebtResponseDto.builder()
                        .id(debt.getId())
                        .name(debt.getName())
                        .amount(debt.getAmount())
                        .category(debt.getCategory())
                        .date(debt.getDate())
                        .installments(debt.getInstallments() == null ? null : debt.getInstallments().stream().map(
                                installment -> InstallmentResponseDto.builder()
                                        .installmentAmount(installment.getInstallmentAmount())
                                        .installmentDueDate(installment.getInstallmentDueDate())
                                        .installmentNumber(installment.getInstallmentNumber())
                                        .build()
                        ).toList())
                        .build());
    }

    @PostMapping(value = "/debts", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MessageResponseDto> addDebt(HttpServletRequest request, @Valid @RequestBody DebtRequestDto debtRequest) {

        var userId = request.getAttribute("userId");
        log.info("[DebtController] - Adding debt for userId: {} with details: {}", userId, debtRequest);

        var debt = DebtRegisterDto.builder()
                .name(debtRequest.getName())
                .amount(debtRequest.getAmount())
                .category(debtRequest.getCategory())
                .date(debtRequest.getDate())
                .userId(Long.valueOf(userId.toString()))
                .installmentNumber(debtRequest.getInstallmentNumber())
                .fixed(debtRequest.getFixed())
                .build();

        debtService.createDebtForUser(debt);

        return ResponseEntity
                .status(201)
                .body(MessageResponseDto.builder()
                        .message("Debt added successfully")
                        .build());

    }
}
