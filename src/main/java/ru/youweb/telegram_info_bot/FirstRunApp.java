package ru.youweb.telegram_info_bot;

import ru.youweb.telegram_info_bot.currency.AllCurrencyId;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyId;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.currency.FixerApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirstRunApp {
    WorkDB workDB;
    FixerApi fixerApi;

    public FirstRunApp(WorkDB workDB, FixerApi fixerApi, AllCurrencyId currencyId) throws ExecutionException, InterruptedException {
        this.workDB = workDB;
        this.fixerApi = fixerApi;

        CurrencyRate currency = fixerApi.getCurrencyRate("USD", "latest");
        workDB.addCurrency(currency.getBase());
        for (Map.Entry<String, Double> item : currency.getRates().entrySet())
            workDB.addCurrency(item.getKey());

        currencyId.load();

        long timeStamp = 978296400000l;

        Calendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(timeStamp);

        String date;

        String dateDb;

        CurrencyRate currencyRate;

        while (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTimeInMillis());
            dateDb = new SimpleDateFormat("yyyyMMdd").format(calendar.getTimeInMillis());

            System.out.println("date add " + date);

            for (Map.Entry<String, Double> item : currency.getRates().entrySet()) {
                TimeUnit.SECONDS.sleep(1);
                currencyRate = fixerApi.getCurrencyRate(item.getKey(), date);
                try {
                    for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                        workDB.addCurrencyRate(currencyId.getId(currencyRate.getBase()), currencyId.getId(rates.getKey()), rates.getValue(), dateDb);
                        System.out.println("add " + currencyId.getId(currencyRate.getBase()) + " " + currencyId.getId(rates.getKey()) + " " + rates.getValue());
                    }
                } catch (NullPointerException e) {
                }
            }
            System.out.println("next iteration");
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400000l);
        }
    }
}
