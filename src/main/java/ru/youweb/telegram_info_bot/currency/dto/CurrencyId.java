package ru.youweb.telegram_info_bot.currency.dto;

public class CurrencyId {

    private int id;

    private String nameCurrency;

    public CurrencyId(int id, String nameCurrency) {
        this.id = id;
        this.nameCurrency = nameCurrency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCurrency() {
        return nameCurrency;
    }

    public void setNameCurrency(String nameCurrency) {
        this.nameCurrency = nameCurrency;
    }
}
