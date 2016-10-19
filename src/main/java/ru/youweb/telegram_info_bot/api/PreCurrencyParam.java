package ru.youweb.telegram_info_bot.api;

import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class PreCurrencyParam {

    public Map<String, Object> pre(CurrencyParam param, CurrencyRateDb rateDb, CurrencyDb currencyDb, FixerApi fixerApi, DateTimeFormatter format) {
        Map<String, Object> params = new HashMap<>();
        if (param.getError() == 0) {
            params.put("base", param.getBaseCurrency());
            params.put("date", param.getDate().format(format));
            params.put("listRate", preListCurrency(param, rateDb, currencyDb, fixerApi));
        } else {
            params.put("error", param.getError());
        }
        return params;
    }

    private Map<String, Double> preListCurrency(CurrencyParam param, CurrencyRateDb rateDb, CurrencyDb currencyDb, FixerApi fixerApi) {
        if (param.getDate().isAfter(LocalDate.now()))
            param.setDate(LocalDate.now());
        if (LocalDateTime.now().getHour() < 5)
            param.setDate(LocalDate.now().minusDays(1));
        if (rateDb.issetInfo(param.getBaseCurrency(), param.getDate())) {
            return fromBase(param, rateDb, currencyDb);
        } else {
            return fromFixer(param, fixerApi);
        }
    }

    private Map<String, Double> fromFixer(CurrencyParam param, FixerApi fixerApi) {
        Map<String, Double> listCurrency = new HashMap<>();
        CurrencyRate rate = fixerApi.getCurrencyRate(param.getBaseCurrency(), param.getDate());
        if (param.getListCurrencyName().size() > 0) {
            for (String currency : param.getListCurrencyName()) {
                try {
                    listCurrency.put(currency, rate.getRates().get(currency));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    listCurrency.put(currency, 0.0);
                }
            }
        } else  {
            listCurrency = rate.getRates();
        }
        return  listCurrency;
    }

    private Map<String, Double> fromBase(CurrencyParam param, CurrencyRateDb rateDb, CurrencyDb currencyDb) {
        Map<String, Double> listCurrency = new HashMap<>();
        (param.getListCurrencyName().size() > 0 ? param.getListCurrencyName() : currencyDb.getAllCurrencies()).stream().filter(currency -> !param.getBaseCurrency().equals(currency)).forEach(currency -> {
            listCurrency.put(currency, rateDb.findValue(param.getBaseCurrency(), currency, param.getDate()));
        });
        return listCurrency;
    }
}
