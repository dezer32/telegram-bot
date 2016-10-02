package ru.youweb.telegram_info_bot;

import java.util.Map;

//@TODO Удалить ненужный комментарий
/**
 * Created by Youweb on 29.09.2016.
 */
//@TODO Удалить Struct в конце названия класса, из контекста и так понятно что это класс
public class CurrencyRateStruct {
    private String base;
    private String date;
    private Map<String, Double> rates;

    //@TODO Удалить пустой конструктор, Java и так его создаст при компиляции
    //@TODO мы пишем { в конце строки, а не в начале следующей
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
