package com.noptech.financeapi.controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.noptech.financeapi.entity.User;
import com.noptech.financeapi.dto.DebtsAndInstallmentsDto;
import com.noptech.financeapi.exception.GlobalExceptionHandler;
import com.noptech.financeapi.repository.UserRepository;
import com.noptech.financeapi.service.DebtService;
import com.noptech.financeapi.util.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DebtControllerTest {
    private UserRepository userRepository;
    private DebtService debtService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        debtService = mock(DebtService.class);
        var keycloakId = UUID.randomUUID();
        var user = new User();
        user.setId(1L);
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));
        when(debtService.getDebtsByMonth(anyString(), anyInt(), anyInt())).thenReturn(List.of());
        var jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(keycloakId.toString())
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(new DebtController(userRepository, debtService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(
                        Jackson2ObjectMapperBuilder.json()
                                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                .build()))
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(Jwt.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter,
                                                  ModelAndViewContainer container,
                                                  NativeWebRequest request,
                                                  WebDataBinderFactory factory) {
                        return jwt;
                    }
                })
                .build();
    }

    @Test
    void usesRequestedYearForJanuary2027() throws Exception {
        var installment = DebtsAndInstallmentsDto.builder()
                .id(10L)
                .name("Purchase")
                .amount(new BigDecimal("1200.00"))
                .category(Category.INSTALLMENT_CREDIT)
                .date(LocalDate.of(2026, 10, 1))
                .installmentAmount(new BigDecimal("100.00"))
                .installmentNumber(3)
                .installmentDueDate(LocalDate.of(2027, 1, 1))
                .build();
        when(debtService.getDebtsByMonth("1", 2027, 1)).thenReturn(List.of(installment));
        mockMvc.perform(get("/v1/debts/month/1").param("year", "2027"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].category").value("INSTALLMENT_CREDIT"))
                .andExpect(jsonPath("$[0].installmentAmount").value(100.00))
                .andExpect(jsonPath("$[0].installmentNumber").value(3))
                .andExpect(jsonPath("$[0].installmentDueDate").value("2027-01-01"));
        verify(debtService).getDebtsByMonth("1", 2027, 1);
    }

    @Test
    void supportsPreviousYears() throws Exception {
        mockMvc.perform(get("/v1/debts/month/12").param("year", "2025"))
                .andExpect(status().isOk());
        verify(debtService).getDebtsByMonth("1", 2025, 12);
    }

    @Test
    void defaultsToCurrentYearWhenOmitted() throws Exception {
        var currentYear = LocalDate.now().getYear();
        mockMvc.perform(get("/v1/debts/month/10"))
                .andExpect(status().isOk());
        verify(debtService).getDebtsByMonth("1", currentYear, 10);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 13})
    void rejectsInvalidMonths(int month) throws Exception {
        mockMvc.perform(get("/v1/debts/month/" + month).param("year", "2027"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Month must be between 1 and 12"));
        verifyNoInteractions(debtService);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 10000})
    void rejectsInvalidYears(int year) throws Exception {
        mockMvc.perform(get("/v1/debts/month/1").param("year", Integer.toString(year)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Year must be between 1 and 9999"));
        verifyNoInteractions(debtService);
    }
}
