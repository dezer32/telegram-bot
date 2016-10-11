package ru.youweb.telegram_info_bot;

import com.typesafe.config.Config;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimerTask;

public class SchedulerTask extends TimerTask {

    private FixerApi fixerApi;

    private CurrencyRateDb currencyRateDb;

    private CurrencyDb currencyDb;

    private CurrencyRate currencyRate;

    private LocalDate date;

    private DateTimeFormatter format;


    public SchedulerTask(FixerApi fixerApi, CurrencyRateDb currencyRateDb, CurrencyDb currencyDb, Config config) {
        this.fixerApi = fixerApi;
        this.currencyRateDb = currencyRateDb;
        this.currencyDb = currencyDb;
        this.format = DateTimeFormatter.ofPattern(config.getString("db.dateFormat"));
    }

    public void run() {
        date = LocalDate.now();

        for (String cur : currencyDb.getAllCurrency()) {
            try {
                currencyRate = fixerApi.getCurrencyRate(cur);
                for (Map.Entry<String, Double> rate : currencyRate.getRates().entrySet()) {
                    currencyRateDb.update(currencyRate.getBase(), rate.getKey(), rate.getValue(), date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
