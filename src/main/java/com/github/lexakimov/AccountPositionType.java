package com.github.lexakimov;

/**
 * @author akimov
 * created at: 04.01.2023 11:54
 */
public enum AccountPositionType {
    OPENING_AN_ACCOUNT("Открытие счета"),
    ENTERING_METER_READINGS("Передача показания прибора учета"),
    RATE_CHANGE("Смена тарифа"),
    PAYMENT("Оплата услуг");

    private final String message;

    AccountPositionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
