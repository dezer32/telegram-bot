package ru.youweb.telegram_info_bot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.youweb.telegram_info_bot.api.BotApplicationImpl;
import ru.youweb.telegram_info_bot.db.UserDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Injector injector = Guice.createInjector(new TelegramBotModule());

        TelegramApi tApi = injector.getInstance(TelegramApi.class);

        UserDb userDb = injector.getInstance(UserDb.class);

        //injector.getInstance(FirstRunApp.class);

        CurrencyUpdateDailyService st = injector.getInstance(CurrencyUpdateDailyService.class);
        st.startAsync();

        BotApplicationImpl botApp = injector.getInstance(BotApplicationImpl.class);

        botApp.run();
    }
}
