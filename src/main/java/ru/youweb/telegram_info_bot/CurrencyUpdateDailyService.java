package ru.youweb.telegram_info_bot;

import com.github.racc.tscg.TypesafeConfig;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MINUTES;

public class CurrencyUpdateDailyService extends AbstractScheduledService {

    private FixerApi fixerApi;

    private CurrencyRateDb currencyRateDb;

    private CurrencyDb currencyDb;

    private Duration schedulePeriod;

    private long initDelay;

    @Inject
    public CurrencyUpdateDailyService(FixerApi fixerApi, CurrencyRateDb currencyRateDb, CurrencyDb currencyDb,
                                      @TypesafeConfig("timer.schedulePeriod") Duration schedulePeriod,
                                      @TypesafeConfig("timer.scheduleStart") String scheduleStart) {
        this.fixerApi = fixerApi;
        this.currencyRateDb = currencyRateDb;
        this.currencyDb = currencyDb;
        this.schedulePeriod = schedulePeriod;
        LocalTime scheduleStartTime = LocalTime.parse(scheduleStart);
        LocalDateTime now = LocalDateTime.now();
        if (now.toLocalTime().isAfter(scheduleStartTime)) {
            this.initDelay = MINUTES.between(now, now.with(scheduleStartTime).plusDays(1));
        } else {
            this.initDelay = MINUTES.between(now, now.with(scheduleStartTime));
        }
    }

    @Override
    protected void runOneIteration() throws Exception {
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

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(initDelay, schedulePeriod.toMinutes(), TimeUnit.MINUTES);
    }

    @Override
    protected String serviceName() {
        return "currency_update_daily_service";
    }

}
