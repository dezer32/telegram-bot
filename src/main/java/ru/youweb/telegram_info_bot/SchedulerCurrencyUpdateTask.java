package ru.youweb.telegram_info_bot;

import com.google.inject.Inject;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.LocalDate;
import java.util.Map;
import java.util.TimerTask;

public class SchedulerCurrencyUpdateTask extends TimerTask {

    private FixerApi fixerApi;

    private CurrencyRateDb currencyRateDb;

    private CurrencyDb currencyDb;

    @Inject
    public SchedulerCurrencyUpdateTask(FixerApi fixerApi, CurrencyRateDb currencyRateDb, CurrencyDb currencyDb) {
        this.fixerApi = fixerApi;
        this.currencyRateDb = currencyRateDb;
        this.currencyDb = currencyDb;
    }

    public void run() {
        LocalDate date = LocalDate.now();

        for (String currency : currencyDb.getAllCurrencies()) {
            try {
                CurrencyRate currencyRate = fixerApi.getCurrencyRate(currency);
                for (Map.Entry<String, Double> rate : currencyRate.getRates().entrySet()) {
                    currencyRateDb.update(currencyRate.getBase(), rate.getKey(), rate.getValue(), date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
