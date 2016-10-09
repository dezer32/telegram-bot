package ru.youweb.telegram_info_bot.currency.dto;

import java.util.Map;

public class CurrencyRate {

    private String base;

    private String date;

    private Map<String, Double> rates;

    //@TODO Удалить пустой конструктор, Java и так его создаст при компиляции
    //@TODO мы пишем { в конце строки, а не в начале следующей
    public CurrencyRate() {

    }

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
