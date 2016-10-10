package ru.youweb.telegram_info_bot;

import com.typesafe.config.Config;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.currency.FixerApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirstRunApp {

    public FirstRunApp(WorkDB workDB, FixerApi fixerApi, Config config) throws ExecutionException, InterruptedException {

        LocalDate dateParse = LocalDate.parse(config.getString("firstRun.date"));

        Duration duration = Duration.parse(config.getString("firstRun.period"));

        CurrencyRate currencyRate;

        while (dateParse.isBefore(LocalDate.now())) {
            for (String currency: workDB.getAllCurrency()) {
                currencyRate = fixerApi.getCurrencyRate(currency, dateParse);
                if (!currencyRate.isEmpty()) {
                    for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                        workDB.addCurrencyRate(currencyRate.getBase(), rates.getKey(), rates.getValue(), dateParse);
                    }
                }
            }
            dateParse.plusDays(duration.toDays());
        }
    }
}
