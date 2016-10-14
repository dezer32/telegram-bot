package ru.youweb.telegram_info_bot.api;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.typesafe.config.Config;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BotApplicationImpl extends BotApplication {
    Config config;

    CurrencyRateDb rateDb;

    @Inject
    public BotApplicationImpl(TelegramApi telegramApi, PebbleEngine pebbleEngine, Config config, CurrencyRateDb rateDb) {
        super(telegramApi, pebbleEngine);
        this.config = config;
        this.rateDb = rateDb;
    }

    @Override
    protected void configure() {
        on("/c", (request, response) -> {
            CurrencyParam message = new ParseParam().parse(request.getParams().get(0), DateTimeFormatter.ofPattern(config.getString("dateFormatMessage")));
            if (message.getError() == 0) {
                response.setContent(new PreCurrencyParam().pre(message, rateDb));
            }

        });
        on("/h", (request, response) -> {

        });
    }
}
