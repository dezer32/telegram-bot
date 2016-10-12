package ru.youweb.telegram_info_bot.api;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.PebbleEngine;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

public class BotApplicationImpl extends BotApplication {

    @Inject
    public BotApplicationImpl(TelegramApi telegramApi, PebbleEngine pebbleEngine) {
        super(telegramApi, pebbleEngine);
    }

    @Override
    protected void configure() {
        on("/c", (request, response) -> {

        });
        on("/h", (request, response) -> {

        });
    }
}
