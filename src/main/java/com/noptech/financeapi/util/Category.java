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
    OTHER(5),
    INSTALLMENT_CREDIT(6);

    private final Integer categoryNumber;

    public static Category fromCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("Category code Not Null");
        }

        for (Category c : values()) {
            if (c.categoryNumber.equals(code)) {
                return c;
            }
        }

        throw new IllegalArgumentException("Invalid Category code: " + code);
    }
}
