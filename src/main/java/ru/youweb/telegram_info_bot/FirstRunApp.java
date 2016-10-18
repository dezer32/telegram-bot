package ru.youweb.telegram_info_bot;

import com.github.racc.tscg.TypesafeConfig;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirstRunApp extends AbstractIdleService {

    private CurrencyDb currencyDb;

    private CurrencyRateDb currencyRateDb;

    private FixerApi fixerApi;

    private String date;

    private Logger log;

    @Inject
    public FirstRunApp(CurrencyDb currencyDb, CurrencyRateDb currencyRateDb, FixerApi fixerApi, @TypesafeConfig("firstRun.date") String date, Logger log) {
        this.currencyDb = currencyDb;
        this.currencyRateDb = currencyRateDb;
        this.fixerApi = fixerApi;
        this.date = date;
        this.log = log;
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Начало начального импорта.");
        LocalDate dateParse = LocalDate.parse(date);
        CurrencyRate currencyRate = fixerApi.getCurrencyRate("EUR");
        if (!currencyRate.isEmpty()) {
            for (Map.Entry<String, Double> rates : currencyRate.getRates().entrySet()) {
                currencyRateDb.add(currencyRate.getBase(), rates.getKey(), rates.getValue(), dateParse);
            }
        }

        for (LocalDate end = LocalDate.now(); dateParse.isBefore(end); dateParse = dateParse.plusDays(1)) {
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
        log.info("Конец начального импорта.");
    }

    @Override
    protected void shutDown() throws Exception {

    }
}
