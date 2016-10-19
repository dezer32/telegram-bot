package ru.youweb.telegram_info_bot.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ParseParam {

    public CurrencyParam parse(String message, DateTimeFormatter format) {
        return new CurrencyParam(message, format);
    }
}

class CurrencyParam {
    private String baseCurrency;

    private List<String> listCurrencyName = new ArrayList<>();

    private LocalDate date;

    private DateTimeFormatter formatMessage;

    private Integer error = 0;

    public CurrencyParam(String message, DateTimeFormatter formatMessage) {
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
        return date != null ? date : LocalDate.now();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getError() {
        return error;
    }

    private void parse(String parseMessage) {
        if (parseMessage != null && !"".equals(parseMessage)) {
            if (isInt(String.valueOf(parseMessage.charAt(0)))) {
                if (parseMessage.length() == 8 && isInt(parseMessage)) {
                    try {
                        date = LocalDate.parse(parseMessage, formatMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error = 2;
                    }
                } else {
                    error = 2;
                }
            } else {
                if (parseMessage.length() >= 3) {
                    String val = "";
                    for (int i = 0; i < 3; i++) {
                        val += String.valueOf(parseMessage.charAt(i));
                    }
                    if (listCurrencyName.contains(val)) {
                        error = 3;
                    } else {
                        if (baseCurrency == null || "".equals(baseCurrency)) {
                            baseCurrency = val;
                        } else {
                            listCurrencyName.add(val);
                        }
                        parseMessage = parseMessage.substring(3);
                        if (parseMessage.length() > 2) {
                            parse(parseMessage);
                        }
                    }
                } else {
                    error = 1;
                }
            }
        } else {
            error = 1;
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

