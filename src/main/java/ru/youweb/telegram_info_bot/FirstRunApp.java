package ru.youweb.telegram_info_bot;

import com.typesafe.config.Config;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.WorkDB;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirstRunApp {

    public FirstRunApp(WorkDB workDB, FixerApi fixerApi, Config config) throws ExecutionException, InterruptedException {

        LocalDate dateParse = LocalDate.parse(config.getString("firstRun.date"));

        Duration duration = Duration.parse(config.getString("firstRun.period"));

        CurrencyRate currencyRate;

        currencyRate = fixerApi.getCurrencyRate("USD");
        if (!currencyRate.isEmpty()) {
            for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                workDB.CurrencyRateDb().addCurrencyRate(currencyRate.getBase(), rates.getKey(), rates.getValue(), dateParse);
            }
        }

        while (dateParse.isBefore(LocalDate.now())) {
            for (String currency: workDB.CurrencyDb().getAllCurrency()) {
                currencyRate = fixerApi.getCurrencyRate(currency, dateParse);
                if (!currencyRate.isEmpty()) {
                    for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                        workDB.CurrencyRateDb().addCurrencyRate(currencyRate.getBase(), rates.getKey(), rates.getValue(), dateParse);
                    }
                }
            }
            TimeUnit.MILLISECONDS.sleep(10);
            dateParse.plusDays(duration.toDays());
        }
    }
}
