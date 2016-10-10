package ru.youweb.telegram_info_bot;

import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.db.WorkDB;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimerTask;

public class SchedulerTask extends TimerTask {

    private FixerApi fixerApi;

    private WorkDB workDB;

    private CurrencyRate currencyRate;

    private LocalDate date;

    private DateTimeFormatter format;

    public SchedulerTask(FixerApi fixerApi, WorkDB workDB, DateTimeFormatter format) {
        this.fixerApi = fixerApi;
        this.workDB = workDB;
        this.format = format;
    }

    public void run() {
        date = LocalDate.now();

        for (String cur : workDB.CurrencyDb().getAllCurrency()) {
            try {
                currencyRate = fixerApi.getCurrencyRate(cur);
                for (Map.Entry<String, Double> rate : currencyRate.getRates().entrySet()) {
                    workDB.CurrencyRateDb().updateCurrencyRate(currencyRate.getBase(), rate.getKey(), rate.getValue(), date.format(format));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
