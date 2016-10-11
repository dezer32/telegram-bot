package ru.youweb.telegram_info_bot;

import com.typesafe.config.Config;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirstRunApp {

    public FirstRunApp(CurrencyRateDb currencyRateDb, CurrencyDb currencyDb, FixerApi fixerApi, Config config) throws ExecutionException, InterruptedException {
        LocalDate dateParse = LocalDate.parse(config.getString("firstRun.date"));
        Duration duration = Duration.parse(config.getString("firstRun.period"));
        CurrencyRate currencyRate = fixerApi.getCurrencyRate("USD");
        if (!currencyRate.isEmpty()) {
            for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                currencyRateDb.add(currencyRate.getBase(), rates.getKey(), rates.getValue(), dateParse);
            }
        }

        for (LocalDate end = LocalDate.now(); dateParse.isBefore(end); dateParse.plusDays(duration.toDays())) {
            for (String currency : currencyDb.getAllCurrencies()) {
                currencyRate = fixerApi.getCurrencyRate(currency, dateParse);
                if (!currencyRate.isEmpty()) {
                    for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                        currencyRateDb.add(currencyRate.getBase(), rates.getKey(), rates.getValue(), dateParse);
                    }
                }
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }
}
