package ru.youweb.telegram_info_bot.currency.dto;

import java.util.Map;

public class CurrencyRate {

    private String base;

    private String date;

    private Map<String, Double> rates;

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public boolean isEmpty() {
        return rates == null || rates.size() == 0;
    }
}
