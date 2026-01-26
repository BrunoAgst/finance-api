package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.dto.DebtConsultDto;
import com.noptech.financeapi.dto.DebtRegisterDto;
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
            if (debtRegisterDto.getIsInstallment()) {
                createInstallmentDebts(debtRegisterDto);
                return;
            }

            var data = new Debt();
            data.setName(debtRegisterDto.getName());
            data.setAmount(debtRegisterDto.getAmount());
            data.setCategory(debtRegisterDto.getCategory().getCategoryName());
            data.setUserId(debtRegisterDto.getUserId());
            data.setDueDate(debtRegisterDto.getDueDate());
            data.setIsInstallment(false);
            data.setFixed(debtRegisterDto.getFixed());

            debtRepository.save(data);
            log.info("[DebtService] Debt created for userId: {}", debtRegisterDto.getUserId());

        } catch (Exception e) {
            log.error("[DebtService] Error creating debt for userId: {}. Error: {}", debtRegisterDto.getUserId(), e.getMessage());
            throw new DataAccessException("Error creating debt: " + e.getMessage());
        }
    }

    @Override
    public DebtConsultDto getDebtsById(String userId, Long debtId) {
        try {
            var debt = debtRepository.findByIdAndUserId(debtId, Long.valueOf(userId));
            if (debt.isEmpty()) {
                log.error("[DebtService] Debt not found for userId: {} and debtId: {}", userId, debtId);
                throw new NotFoundException("Debt not found");
            }

            if(debt.get().getIsInstallment()) {
                var installments = installmentRepository.findByInstallmentId(debt.get().getInstallmentId())
                        .orElseThrow(() -> new DataAccessException("Error fetching installments"));

                if(installments.isEmpty() || installments.getFirst().getInstallmentId() == null) {
                    log.error("[DebtService] Installments not found for installmentId: {}", debt.get().getInstallmentId());
                    throw new NotFoundException("Installments not found");
                }

                log.info("[DebtService] Debt fetched for userId: {}, debtId: {} and installmentId: {}", userId, debtId, installments.getFirst().getInstallmentId());

                return DebtConsultDto.builder()
                        .id(debt.get().getId())
                        .name(debt.get().getName())
                        .amount(debt.get().getAmount())
                        .category(Category.fromCode(debt.get().getCategory()))
                        .dueDate(debt.get().getDueDate())
                        .installmentId(debt.get().getInstallmentId())
                        .installments(installments.stream().map(i -> {
                            return DebtConsultDto.Installment.builder()
                                    .installmentNumber(i.getInstallmentNumber())
                                    .installmentAmount(i.getInstallmentAmount())
                                    .installmentDueDate(i.getInstallmentDueDate())
                                    .installmentId(i.getInstallmentId())
                                    .build();
                        }).toList())
                        .build();
            }

            log.info("[DebtService] Debt fetched for userId: {} and debtId: {}", userId, debtId);

            return DebtConsultDto.builder()
                    .id(debt.get().getId())
                    .name(debt.get().getName())
                    .amount(debt.get().getAmount())
                    .category(Category.fromCode(debt.get().getCategory()))
                    .dueDate(debt.get().getDueDate())
                    .build();

        } catch (Exception e) {
            log.error("[DebtService] Error fetching debt for userId: {} and debtId: {}. Error: {}", userId, debtId, e.getMessage());
            throw new DataAccessException("Error fetching debt: " + e.getMessage());
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
        data.setCategory(debtRegisterDto.getCategory().getCategoryName());
        data.setUserId(debtRegisterDto.getUserId());
        data.setDueDate(debtRegisterDto.getDueDate());
        data.setIsInstallment(true);
        data.setInstallmentId(installmentID);
        data.setFixed(false);
        debtRepository.save(data);
        log.info("[DebtService]  Debt created for userId: {} with Installment ID: {}", debtRegisterDto.getUserId(), installmentID);

        for (int i = 0; i < debtRegisterDto.getInstallmentNumber(); i++) {

            var installmentData = new Installment();
            installmentData.setInstallmentAmount(installmentAmount);
            installmentData.setInstallmentNumber(i + 1);
            installmentData.setInstallmentDueDate(debtRegisterDto.getDueDate().plusMonths(i + 1));
            installmentData.setInstallmentId(installmentID);
            installmentRepository.save(installmentData);
            log.info("[DebtService] Installment debt created for userId: {} - Installment {}", debtRegisterDto.getUserId(), (i + 1));
        }
    }
}
