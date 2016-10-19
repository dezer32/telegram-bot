package ru.youweb.telegram_info_bot.api;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.time.format.DateTimeFormatter;

public class BotApplicationImpl extends BotApplication {

    private Config config;

    private CurrencyRateDb rateDb;

    private CurrencyDb currencyDb;

    private Logger log;

    private FixerApi fixerApi;

    @Inject
    public BotApplicationImpl(TelegramApi telegramApi, PebbleEngine pebbleEngine, Config config, CurrencyRateDb rateDb, CurrencyDb currencyDb, Logger log, FixerApi fixerApi) {
        super(telegramApi, pebbleEngine);
        this.config = config;
        this.rateDb = rateDb;
        this.currencyDb = currencyDb;
        this.log = log;
        this.fixerApi = fixerApi;
    }

    @Override
    protected void configure() {
        on("/c", (request, response) -> {
            CurrencyParam message = new ParseParam().parse(request.getFirstParam().toUpperCase(), DateTimeFormatter.ofPattern(config.getString("dateFormatMessage")));
            log.info("Команда: /c");
            if (message.getError() == 0) {
                log.info("Базовая валюта: " + message.getBaseCurrency());
                log.info("Дата: " + message.getDate());
                if (message.getListCurrencyName().size() == 0) {
                    log.info("Относительно всех валют");
                } else {
                    log.info("Вторая валюта: " + message.getListCurrencyName().size());
                }
            } else {
                log.info("Код ошибки: " + message.getError());
            }
            response.setView(config.getString("tmpl.answer"), new PreCurrencyParam().pre(message, rateDb, currencyDb, fixerApi, DateTimeFormatter.ofPattern(config.getString("dateFormatAnswer"))));
        });
        on("/h", (request, response) -> {
            response.setView(config.getString("tmpl.help"));
        });

        on("/start", (request, response) -> {
            response.setView((config.getString("tmpl.welcome")));
        });
    }
}
