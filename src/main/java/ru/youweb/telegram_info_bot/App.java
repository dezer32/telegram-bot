package ru.youweb.telegram_info_bot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.flywaydb.core.Flyway;
import ru.youweb.telegram_info_bot.api.BotApplicationImpl;

import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Injector injector = Guice.createInjector(new TelegramBotModule());

        Flyway flyway = injector.getInstance(Flyway.class);
        flyway.migrate();

        FirstRunApp firstRunApp = injector.getInstance(FirstRunApp.class);
        firstRunApp.startAsync();

        CurrencyUpdateDailyService st = injector.getInstance(CurrencyUpdateDailyService.class);
        st.startAsync();

        BotApplicationImpl botApp = injector.getInstance(BotApplicationImpl.class);
        botApp.run();
    }
}
