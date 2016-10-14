package ru.youweb.telegram_info_bot.api;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.time.format.DateTimeFormatter;

public class BotApplicationImpl extends BotApplication {
    Config config;

    CurrencyRateDb rateDb;

    CurrencyDb currencyDb;

    Logger log;

    @Inject
    public BotApplicationImpl(TelegramApi telegramApi, PebbleEngine pebbleEngine, Config config, CurrencyRateDb rateDb, CurrencyDb currencyDb, Logger logger) {
        super(telegramApi, pebbleEngine);
        this.config = config;
        this.rateDb = rateDb;
        this.currencyDb = currencyDb;
        this.log = logger;
    }

    @Override
    protected void configure() {
        on("/c", (request, response) -> {
            CurrencyParam message = new ParseParam().parse(request.getFirstParam(), DateTimeFormatter.ofPattern(config.getString("dateFormatMessage")));
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
            response.setView(config.getString("tmpl.answer"), new PreCurrencyParam().pre(message, rateDb, currencyDb, DateTimeFormatter.ofPattern(config.getString("dateFormatAnswer"))));
        });
        on("/h", (request, response) -> {
            response.setView(config.getString("tmpl.help"));
        });
    }
}
