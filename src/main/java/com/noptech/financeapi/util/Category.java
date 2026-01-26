package com.noptech.financeapi.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    CREDIT(1),
    BILLET(2),
    DEBIT(3),
    PIX(4),
    OTHER(5);

    private final Integer categoryName;
}
