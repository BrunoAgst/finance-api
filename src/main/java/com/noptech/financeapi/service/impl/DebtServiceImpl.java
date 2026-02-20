package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.dto.*;
import com.noptech.financeapi.entity.Debt;
import com.noptech.financeapi.entity.Installment;
import com.noptech.financeapi.exception.DataAccessException;
import com.noptech.financeapi.exception.NotFoundException;
import com.noptech.financeapi.repository.DebtRepository;
import com.noptech.financeapi.repository.InstallmentRepository;
import com.noptech.financeapi.service.DebtService;
import com.noptech.financeapi.util.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final InstallmentRepository installmentRepository;

    @Override
    public void createDebtForUser(DebtRegisterDto debtRegisterDto) {

        try {
            if (debtRegisterDto.getCategory().name().equals("INSTALLMENT_CREDIT")) {
                createInstallmentDebts(debtRegisterDto);
                return;
            }

            var data = new Debt();
            data.setName(debtRegisterDto.getName());
            data.setAmount(debtRegisterDto.getAmount());
            data.setCategory(debtRegisterDto.getCategory().getCategoryNumber());
            data.setUserId(debtRegisterDto.getUserId());
            data.setDate(debtRegisterDto.getDate());
            data.setFixed(debtRegisterDto.getFixed());

            debtRepository.save(data);
            log.info("[DebtService] Debt created for userId: {}", debtRegisterDto.getUserId());

        } catch (Exception e) {
            log.error("[DebtService] Error creating debt for userId: {}. Error: {}", debtRegisterDto.getUserId(), e.getMessage());
            throw new DataAccessException("Error creating debt: " + e.getMessage());
        }
    }

    @Override
    public DebtDto getDebtsById(String userId, Long debtId) {
        try {
            var debt = debtRepository.findByIdAndUserId(debtId, Long.valueOf(userId));
            if (debt == null) {
                log.error("[DebtService] Debt not found for userId: {} and debtId: {}", userId, debtId);
                throw new NotFoundException("Debt not found");
            }

            if(debt.getCategory().equals(Category.INSTALLMENT_CREDIT.getCategoryNumber())) {
                var installments = installmentRepository.findByDebtId(debt.getId());

                if(installments.isEmpty() || installments.getFirst().getId() == null) {
                    log.error("[DebtService] Installments not found for installmentId: {}", debt.getId());
                    throw new NotFoundException("Installments not found");
                }

                log.info("[DebtService] Debt fetched for userId: {}, debtId: {} and installmentId: {}", userId, debtId, installments.getFirst().getId());

                return DebtDto.builder()
                        .id(debt.getId())
                        .name(debt.getName())
                        .amount(debt.getAmount())
                        .category(Category.fromCode(debt.getCategory()))
                        .date(debt.getDate())
                        .fixed(debt.getFixed())
                        .installments(installments.stream().map(i ->
                                DebtDto.Installment.builder()
                                    .installmentNumber(i.getInstallmentNumber())
                                    .installmentAmount(i.getInstallmentAmount())
                                    .installmentDueDate(i.getInstallmentDueDate())
                                    .build()
                        ).toList())
                        .build();
            }

            log.info("[DebtService] Debt fetched for userId: {} and debtId: {}", userId, debtId);

            return DebtDto.builder()
                    .id(debt.getId())
                    .name(debt.getName())
                    .amount(debt.getAmount())
                    .category(Category.fromCode(debt.getCategory()))
                    .date(debt.getDate())
                    .fixed(debt.getFixed())
                    .build();

        } catch (Exception e) {
            log.error("[DebtService] Error fetching debt for userId: {} and debtId: {}. Error: {}", userId, debtId, e.getMessage());
            throw new DataAccessException("Error fetching debt: " + e.getMessage());
        }
    }

    @Override
    public List<DebtsAndInstallmentsDto> getAllDebtsForUser(String userId) {
        try {
            var debts = debtRepository.findDebtsByUserIdLast30Days(Long.valueOf(userId));

            log.info("[DebtService] Fetched {} debts for userId: {}", debts.size(), userId);

            return debts.stream()
                    .map(item -> DebtsAndInstallmentsDto.builder()
                            .id(item.getId())
                            .name(item.getDebtName())
                            .amount(item.getDebtAmount())
                            .category(Category.fromCode(item.getCategory()))
                            .date(item.getDebtDate())
                            .fixed(item.getFixed())
                            .installmentAmount(item.getInstallmentAmount())
                            .installmentNumber(item.getInstallmentNumber())
                            .installmentDueDate(item.getInstallmentDueDate())
                            .build()).toList();

        } catch (Exception e) {
            log.error("[DebtService] Error fetching all debts for userId: {}. Error: {}", userId, e.getMessage());
            throw new DataAccessException("Error fetching all debts: " + e.getMessage());
        }

    }

    @Override
    public List<DebtsAndInstallmentsDto> getDebtsByMonth(String userId, Integer year, Integer month) {
        try {
            var debts = debtRepository.findDebtsByUserIdAndMonth(Long.valueOf(userId), year, month);

            log.info("[DebtService] Fetched {} debts for userId: {} for month: {}/{}", debts.size(), userId, month, year);

            return debts.stream()
                    .map(item -> DebtsAndInstallmentsDto.builder()
                            .id(item.getId())
                            .name(item.getDebtName())
                            .amount(item.getDebtAmount())
                            .category(Category.fromCode(item.getCategory()))
                            .date(item.getDebtDate())
                            .fixed(item.getFixed())
                            .installmentAmount(item.getInstallmentAmount())
                            .installmentNumber(item.getInstallmentNumber())
                            .installmentDueDate(item.getInstallmentDueDate())
                            .build()).toList();

        } catch (Exception e) {
            log.error("[DebtService] Error fetching debts by month for userId: {}, year: {}, month: {}. Error: {}", userId, year, month, e.getMessage());
            throw new DataAccessException("Error fetching debts by month: " + e.getMessage());
        }
    }

    @Override
    public void deleteDebtById(String userId, Long debtId) {
        try {
            var debt = debtRepository.findByIdAndUserId(debtId, Long.valueOf(userId));
            if (debt == null) {
                log.error("[DebtService] Debt not found for deletion for userId: {} and debtId: {}", userId, debtId);
                throw new NotFoundException("Debt not found for deletion");
            }
            debtRepository.deleteById(debtId);
            log.info("[DebtService] Debt deleted for userId: {} and debtId: {}", userId, debtId);
        } catch (Exception e) {
            log.error("[DebtService] Error deleting debt for userId: {} and debtId: {}. Error: {}", userId, debtId, e.getMessage());
            throw new DataAccessException("Error deleting debt: " + e.getMessage());
        }
    }

    @Override
    public DebtDto updateDebtById(String userId, Long debtId, DebtUpdateDto debtRegisterDto) {
        try {

            var debt = debtRepository.findByIdAndUserId(debtId, Long.valueOf(userId));
            if (debt == null) {
                log.error("[DebtService] Debt not found for update for userId: {} and debtId: {}", userId, debtId);
                throw new NotFoundException("Debt not found for update");
            }

            debtRepository.updateDebtById(
                    debtId,
                    debtRegisterDto.getName(),
                    debtRegisterDto.getAmount(),
                    debtRegisterDto.getDate(),
                    debtRegisterDto.getFixed()
            );

            log.info("[DebtService] Debt updated for userId: {} and debtId: {}", userId, debtId);

            if (debt.getCategory().equals(6)) {

                installmentRepository.deleteAllByDebtId(debtId);
                log.info("[DebtService] Existing installments deleted for userId: {} and debtId: {}", userId, debtId);
                List<DebtUpdateDto.Installment> updatedInstallments = new ArrayList<>();

                var debtDate = debtRegisterDto.getDate();

                for (int i = 0; i < debtRegisterDto.getInstallments().size(); i++) {
                    var installmentData = new Installment();
                    installmentData.setInstallmentAmount(debtRegisterDto.getInstallments().get(i).getInstallmentAmount());
                    installmentData.setInstallmentNumber(i + 1);
                    installmentData.setInstallmentDueDate(debtDate.plusMonths(i + 1));
                    installmentData.setDebtId(debtId);
                    installmentRepository.save(installmentData);

                    updatedInstallments.add(DebtUpdateDto.Installment.builder()
                            .installmentNumber(installmentData.getInstallmentNumber())
                            .installmentDueDate(installmentData.getInstallmentDueDate())
                            .installmentAmount(installmentData.getInstallmentAmount())
                            .build());

                    log.info("[DebtService] Installment updated for userId: {} - Installment {}", userId, installmentData.getInstallmentNumber());
                }

                return DebtDto.builder()
                        .id(debtId)
                        .name(debtRegisterDto.getName())
                        .amount(debtRegisterDto.getAmount())
                        .category(Category.fromCode(debt.getCategory()))
                        .date(debtRegisterDto.getDate())
                        .fixed(debtRegisterDto.getFixed())
                        .installments(updatedInstallments.stream().map(installmentDto ->
                                        DebtDto.Installment.builder()
                                                .installmentNumber(installmentDto.getInstallmentNumber())
                                                .installmentDueDate(installmentDto.getInstallmentDueDate())
                                                .installmentAmount(installmentDto.getInstallmentAmount())
                                                .build()
                                ).toList())
                        .build();
            }

            return DebtDto.builder()
                    .id(debtId)
                    .name(debtRegisterDto.getName())
                    .amount(debtRegisterDto.getAmount())
                    .category(Category.fromCode(debt.getCategory()))
                    .date(debtRegisterDto.getDate())
                    .fixed(debtRegisterDto.getFixed())
                    .installments(null)
                    .build();

        } catch (Exception e) {
            log.error("[DebtService] Error updating debt for userId: {} and debtId: {}. Error: {}", userId, debtId, e.getMessage());
            throw new DataAccessException("Error updating debt: " + e.getMessage());
        }
    }

    private void createInstallmentDebts(DebtRegisterDto debtRegisterDto) {
        var installmentAmount = debtRegisterDto.getAmount()
                .divide(
                        BigDecimal.valueOf(debtRegisterDto.getInstallmentNumber()),
                        2,
                        RoundingMode.HALF_UP
                );

        var installmentID = UUID.randomUUID().toString();

        var data = new Debt();
        data.setName(debtRegisterDto.getName());
        data.setAmount(debtRegisterDto.getAmount());
        data.setCategory(debtRegisterDto.getCategory().getCategoryNumber());
        data.setUserId(debtRegisterDto.getUserId());
        data.setDate(debtRegisterDto.getDate());
        data.setFixed(false);
        debtRepository.save(data);
        log.info("[DebtService]  Debt created for userId: {} with Installment ID: {}", debtRegisterDto.getUserId(), installmentID);

        for (int i = 0; i < debtRegisterDto.getInstallmentNumber(); i++) {
            var installmentData = new Installment();
            installmentData.setInstallmentAmount(installmentAmount);
            installmentData.setInstallmentNumber(i + 1);
            installmentData.setInstallmentDueDate(debtRegisterDto.getDate().plusMonths(i + 1));
            installmentData.setDebtId(data.getId());
            installmentRepository.save(installmentData);
            log.info("[DebtService] Installment debt created for userId: {} - Installment {}", debtRegisterDto.getUserId(), (i + 1));
        }
    }
}
