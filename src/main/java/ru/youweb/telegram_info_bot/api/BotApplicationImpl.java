package ru.youweb.telegram_info_bot.api;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.typesafe.config.Config;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BotApplicationImpl extends BotApplication {
    Config config;

    CurrencyRateDb rateDb;

    CurrencyDb currencyDb;

    @Inject
    public BotApplicationImpl(TelegramApi telegramApi, PebbleEngine pebbleEngine, Config config, CurrencyRateDb rateDb, CurrencyDb currencyDb) {
        super(telegramApi, pebbleEngine);
        this.config = config;
        this.rateDb = rateDb;
        this.currencyDb = currencyDb;
    }



    @Override
    public void run() {
        super.run();
    }

    @Override
    protected void configure() {
        on("/c", (request, response) -> {
            CurrencyParam message = new ParseParam().parse(request.getFirstParam(), DateTimeFormatter.ofPattern(config.getString("dateFormatMessage")));
            response.setView(config.getString("tmpl.answer"), new PreCurrencyParam().pre(message, rateDb, currencyDb, DateTimeFormatter.ofPattern(config.getString("dateFormatAnswer"))));
        });
        on("/h", (request, response) -> {
            response.setView(config.getString("tmpl.help"));
        });
    }
}
