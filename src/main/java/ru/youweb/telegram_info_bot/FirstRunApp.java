package ru.youweb.telegram_info_bot;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirstRunApp {
    WorkDB workDB;
    FixerApi fixerApi;

    public FirstRunApp(WorkDB workDB, FixerApi fixerApi) throws ExecutionException, InterruptedException {
        this.workDB = workDB;
        this.fixerApi = fixerApi;

        CurrencyRate currencyRate = fixerApi.getCurrencyRate("USD", "latest");
        workDB.addCurrency(currencyRate.getBase());
        for (Map.Entry<String, Double> item: currencyRate.getRates().entrySet())
            workDB.addCurrency(item.getKey());

        Calendar calendar = new GregorianCalendar();

        currencyRate = fixerApi.getCurrencyRate("USD", "latest");

        calendar.get(Calendar.YEAR);


    }
}
