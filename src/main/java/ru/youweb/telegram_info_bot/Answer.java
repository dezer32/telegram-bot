package ru.youweb.telegram_info_bot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Answer {

    private String command;

    private List<String> currency = new ArrayList<String>();

    private Calendar date = new GregorianCalendar();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private String val = "";

    /**
     * 0 = Все круто
     * 1 = Некоректный формат запроса
     * 2 = Неверный формат даты
     * 3 = Повторяющиеся валюты
     */
    private int error = 0;

    private WorkDB workDB;

    public Answer(WorkDB workDB) {
        this.workDB = workDB;
    }

    /**
     * Читает сообщение и готовит ответ
     * @param mess сообщение от пользователя
     * @return возвращает подготовленный ответ, который годится для отправки пользователю
     */
    public String message(String mess) {
        currency.clear();
        error = 0;
        parse(mess);

        switch (error) {
            case 1:
                return "Некорректные параметры запроса, введите /h для справки.";
            case 2:
                return "Дата указана не верно";
            case 3:
                return "Валюта в списке повторяется";
        }

        if (command.equals("/c")) {
            val = "";
            val += currency.get(0) + "\n";
            val += new SimpleDateFormat("yyyy-MM-dd").format(date.getTimeInMillis()) + "\n";
            for (String cur : (currency.size() > 1 ? currency : workDB.getAllCurrency())) {
                if (cur != currency.get(0)) {
                    try {
                        val += cur + "=" + workDB.getAnswer(currency.get(0), cur, new SimpleDateFormat("yyyyMMdd").format(date.getTimeInMillis())) + "\n";
                    } catch (NullPointerException e) {

                    }
                }
            }
            return val;
        }
        if (command.equals("/h")) {
            return "Бот определяет текущий курс валюты по отношению к другим валютам. \n" +
                    "Доступные команды: \n" +
                    "1. /h - получить текущую справку, \n" +
                    "2. /c {валюта}{список валют для поиска}{дата} - получить информацию о курсе валюты. \n" +
                    "- {валюта} - название валюты(3 символа), \n" +
                    "- {список валют для поиска} - список валют без пробелов, например RUREUR, \n" +
                    "- {дата} - дата за которую нужно получить отношение валют, формат даты yyyyMMdd(например 20101005, 5 октября 2010 года). \n" +
                    "Примеры команды: /c, /c RUB, /c EURRUB, /c EURRUBUSD, /c EUR20100510, /c 20100510";
        }
        return "Некорректные параметры запроса, введите /h для справки";
    }



    public String getCommand() {
        return command;
    }

    public List<String> getCurrency() {
        return currency;
    }

    public Calendar getDate() {
        return date;
    }

    private void parse(String mess) {
        command = mess.split(" ")[0];
        if (!command.equals("/h"))
            try {
                parseValue(mess.split(" ")[1]);
            } catch (Exception e) {
                error = 1;
            }
    }

    private void parseValue(String valueMess) {
        if (isInt(String.valueOf(valueMess.charAt(0)))) {
            if (valueMess.length() == 8 && isInt(valueMess)) {
                try {
                    date.setTime(sdf.parse(valueMess));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                error = 2;
            }
        } else {
            if (valueMess.length() >= 3) {
                for (int i = 0; i < 3; i++) {
                    val += String.valueOf(valueMess.charAt(i));
                }
                if (currency.contains(val)) {
                    error = 3;
                } else {
                    currency.add(val);
                    val = "";
                    valueMess = valueMess.substring(3);
                    if (valueMess.length() > 2) {
                        parseValue(valueMess);
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

    private char[] remove(char[] symbols, int index) {
        if (index >= 0 && index < symbols.length) {
            char[] copy = new char[symbols.length - 1];
            System.arraycopy(symbols, 0, copy, 0, index);
            System.arraycopy(symbols, index + 1, copy, index, symbols.length - index - 1);
            return copy;
        }
        return symbols;
    }
}
