package ru.youweb.telegram_info_bot.api;

import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

class PreCurrencyParam {

    public Map<String, Object> pre(CurrencyParam param, CurrencyRateDb rateDb, CurrencyDb currencyDb, DateTimeFormatter format) {
        Map<String, Object> params = new HashMap<>();
        if (param.getError() == 0) {
            params.put("base", param.getBaseCurrency());
            params.put("date", param.getDate().format(format));
            params.put("listRate", preListCurrency(param, rateDb, currencyDb));
        } else {
            params.put("error", param.getError());
        }
        return params;
    }

    private Map<String, Double> preListCurrency(CurrencyParam param, CurrencyRateDb rateDb, CurrencyDb currencyDb) {
        Map<String, Double> listCurrency = new HashMap<>();
        for (String currency : (param.getListCurrencyName().size() > 0 ? param.getListCurrencyName() : currencyDb.getAllCurrencies())) {
            if (!param.getBaseCurrency().equals(currency)) {
                listCurrency.put(currency, rateDb.findValue(param.getBaseCurrency(), currency, param.getDate()));
            }
        }
        return listCurrency;
    }
}
