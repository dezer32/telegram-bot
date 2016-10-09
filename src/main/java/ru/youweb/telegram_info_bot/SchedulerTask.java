package ru.youweb.telegram_info_bot;

import ru.youweb.telegram_info_bot.currency.AllCurrencyId;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

//@TODO все свойства поментить private
//@TODO работу с датами перевести на java.time.*
public class SchedulerTask extends TimerTask {

    FixerApi fixerApi;

    WorkDB workDB;

    AllCurrencyId currencyId;

    CurrencyRate currencyRate;

    String date;

    //@TODO Удалить AllCurrencyId переписать код без использования этого класса
    public SchedulerTask(FixerApi fixerApi, WorkDB workDB, AllCurrencyId allCurrencyId) {
        this.fixerApi = fixerApi;
        this.workDB = workDB;
        this.currencyId = allCurrencyId;
    }

    public void run() {
        date = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
        for (String cur : currencyId.getAllNameCurrency()) {
            try {
                currencyRate = fixerApi.getCurrencyRate(cur);
                for (Map.Entry<String, Double> rate : currencyRate.getRates().entrySet()) {
                    workDB.updateCurrencyRate(currencyId.getId(currencyRate.getBase()), currencyId.getId(rate.getKey()), rate.getValue(), date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
