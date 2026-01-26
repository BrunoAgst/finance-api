package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.dto.DebtDto;
import com.noptech.financeapi.entity.Debt;
import com.noptech.financeapi.entity.Installment;
import com.noptech.financeapi.exception.DataAccessException;
import com.noptech.financeapi.repository.DebtRepository;
import com.noptech.financeapi.repository.InstallmentRepository;
import com.noptech.financeapi.service.DebtService;
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
    public void createDebtForUser(DebtDto debtDto) {

        try {
            if (debtDto.getIsInstallment()) {
                createInstallmentDebts(debtDto);
                return;
            }

            var data = new Debt();
            data.setName(debtDto.getName());
            data.setAmount(debtDto.getAmount());
            data.setCategory(debtDto.getCategory().getCategoryName());
            data.setUserId(debtDto.getUserId());
            data.setDueDate(debtDto.getDueDate());
            data.setIsInstallment(false);
            data.setFixed(debtDto.getFixed());

            debtRepository.save(data);
            log.info("[DebtService] Debt created for userId: {}", debtDto.getUserId());

        } catch (Exception e) {
            log.error("[DebtService] Error creating debt for userId: {}. Error: {}", debtDto.getUserId(), e.getMessage());
            throw new DataAccessException("Error creating debt: " + e.getMessage());
        }
    }

    private void createInstallmentDebts(DebtDto debtDto) {

        var installmentAmount = debtDto.getAmount()
                .divide(
                        BigDecimal.valueOf(debtDto.getInstallmentNumber()),
                        2,
                        RoundingMode.HALF_UP
                );

        var installmentID = UUID.randomUUID().toString();

        var data = new Debt();
        data.setName(debtDto.getName());
        data.setAmount(debtDto.getAmount());
        data.setCategory(debtDto.getCategory().getCategoryName());
        data.setUserId(debtDto.getUserId());
        data.setDueDate(debtDto.getDueDate());
        data.setIsInstallment(true);
        data.setInstallmentId(installmentID);
        data.setFixed(false);
        debtRepository.save(data);
        log.info("[DebtService]  Debt created for userId: {} with Installment ID: {}", debtDto.getUserId(), installmentID);

        for (int i = 0; i < debtDto.getInstallmentNumber(); i++) {

            var installmentData = new Installment();
            installmentData.setInstallmentAmount(installmentAmount);
            installmentData.setInstallmentNumber(i + 1);
            installmentData.setInstallmentDueDate(debtDto.getDueDate().plusMonths(i + 1));
            installmentData.setInstallmentId(installmentID);
            installmentRepository.save(installmentData);
            log.info("[DebtService] Installment debt created for userId: {} - Installment {}", debtDto.getUserId(), (i + 1));
        }
    }
}
