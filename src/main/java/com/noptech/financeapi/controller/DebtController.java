package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.*;
import com.noptech.financeapi.service.DebtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;


    @PatchMapping(value = "/debts/{debtId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DebtUpdateResponseDto> updateDebtById(HttpServletRequest request, @PathVariable String debtId,
                                                                @Valid @RequestBody DebtUpdateRequestDto debtUpdateRequest) {
        var userId = request.getAttribute("userId");
        log.info("[DebtController] - Updating debt for userId {} and debtId: {} with details: {}", userId, debtId, debtUpdateRequest);

        var installments = debtUpdateRequest.getInstallmentNumber() == null || debtUpdateRequest.getInstallmentNumber() == 0 ? null :
                IntStream.range(1, debtUpdateRequest.getInstallmentNumber() + 1)
                        .mapToObj(i -> DebtUpdateDto.Installment.builder()
                                .installmentNumber(i)
                                .installmentDueDate(debtUpdateRequest.getDate().plusMonths(i + 1))
                                .installmentAmount(
                                        debtUpdateRequest.getAmount().divide(
                                                BigDecimal.valueOf(debtUpdateRequest.getInstallmentNumber()),
                                                2,
                                                RoundingMode.HALF_UP
                                        )
                                )
                                .build())
                        .toList();


        var dataUpdate = DebtUpdateDto.builder()
                .name(debtUpdateRequest.getName())
                .amount(debtUpdateRequest.getAmount())
                .date(debtUpdateRequest.getDate())
                .fixed(debtUpdateRequest.getFixed())
                .installments(installments).build();

        var data = debtService.updateDebtById(userId.toString(), Long.valueOf(debtId), dataUpdate);

        return ResponseEntity
                .status(200)
                .body(DebtUpdateResponseDto.builder()
                        .name(data.getName())
                        .amount(data.getAmount())
                        .category(data.getCategory())
                        .date(data.getDate())
                        .fixed(data.getFixed())
                        .installments(data.getInstallments() == null ? null :
                                data.getInstallments().stream().map(debt ->
                                DebtUpdateResponseDto.Installment.builder()
                                        .installmentNumber(debt.getInstallmentNumber())
                                        .installmentDueDate(debt.getInstallmentDueDate())
                                        .installmentAmount(debt.getInstallmentAmount())
                                        .build()
                        ).toList())
                        .build());
    }

    @DeleteMapping(value = "/debts/{debtId}", produces = "application/json")
    public ResponseEntity<MessageResponseDto> deleteDebtById(HttpServletRequest request, @PathVariable String debtId) {
        var userId = request.getAttribute("userId");
        log.info("[DebtController] - Deleting debt for userId {} and debtId: {}", userId, debtId);

        debtService.deleteDebtById(userId.toString(), Long.valueOf(debtId));
        return ResponseEntity
                .status(200)
                .body(MessageResponseDto.builder()
                        .message("Debt deleted successfully")
                        .build());
    }

    @GetMapping(value = "/debts", produces = "application/json")
    public ResponseEntity<List<AllDebtsResponseDto>> getAllDebts(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        log.info("[DebtController] - Fetching all debts for userId: {}", userId);

        var data = debtService.getAllDebtsForUser(userId.toString());

        return ResponseEntity
                .status(200)
                .body(data.stream().map(item -> AllDebtsResponseDto.builder()
                                .name(item.getName())
                                .amount(item.getAmount())
                                .category(item.getCategory())
                                .date(item.getDate())
                                .fixed(item.getFixed())
                                .installmentAmount(item.getInstallmentAmount())
                                .installmentDueDate(item.getInstallmentDueDate())
                                .installmentNumber(item.getInstallmentNumber())
                                .build()
                ).toList());
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
