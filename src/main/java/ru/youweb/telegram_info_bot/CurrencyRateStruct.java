package ru.youweb.telegram_info_bot;

import java.util.Map;

/**
 * Created by Youweb on 29.09.2016.
 */
public class CurrencyRateStruct {
    private String base;
    private String date;
    private Map<String, Double> rates;

    public CurrencyRateStruct()
    {

    }

    public String getBase()
    {
        return base;
    }

    public Map<String, Double> getRates()
    {
        return rates;
    }
}
