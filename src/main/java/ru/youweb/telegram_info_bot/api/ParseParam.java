package ru.youweb.telegram_info_bot.api;

import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseParam {

    public CurrencyParam parse(String message, DateTimeFormatter format) {
        return new CurrencyParam(message, format);
    }
}

class CurrencyParam {
    private String baseCurrency;

    private List<String> listCurrencyName;

    private LocalDate date;

    private String message;

    private DateTimeFormatter formatMessage;

    private String val;

    private Integer error = 0;

    public CurrencyParam(String message, DateTimeFormatter formatMessage) {
        this.message = message;
        this.formatMessage = formatMessage;

        parse(message);
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public List<String> getListCurrencyName() {
        return listCurrencyName;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getError() {
        return error;
    }

    private void parse(String parseMessage) {
        if (isInt(String.valueOf(parseMessage.charAt(0)))) {
            if (parseMessage.length() == 8 && isInt(parseMessage)) {
                date = LocalDate.parse(parseMessage, formatMessage);
            } else {
                error = 2;
            }
        } else {
            if (parseMessage.length() >= 3) {
                val = "";
                for (int i = 0; i < 3; i++) {
                    val += String.valueOf(parseMessage.charAt(i));
                }
                if (listCurrencyName.contains(val)) {
                    error = 3;
                } else {
                    listCurrencyName.add(val);
                    parseMessage = parseMessage.substring(3);
                    if (parseMessage.length() > 2) {
                        parse(parseMessage);
                    }
                }
            } else {
                error = 1;
            }
        }
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}

class PreCurrencyParam {

    public Map<String, Object> pre(CurrencyParam param, CurrencyRateDb rateDb) {
        Map params = new HashMap<String, Object>();

        params.put("base", param.getBaseCurrency());
        params.put("date", param.getDate());
        params.put("listRate", preListCurrency(param, rateDb));

        return params;
    }

    private Map<String, Double> preListCurrency(CurrencyParam param, CurrencyRateDb rateDb) {
        Map listCurrency = new HashMap<String, Double>();
        Double value;
        for (String currency: param.getListCurrencyName()) {
            value = rateDb.findValue(param.getBaseCurrency(), currency, param.getDate());
            listCurrency.put(currency, value);
        }
        return listCurrency;
    }
}