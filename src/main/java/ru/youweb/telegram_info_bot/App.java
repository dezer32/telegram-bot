package ru.youweb.telegram_info_bot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.youweb.telegram_info_bot.db.UserDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;

import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Injector injector = Guice.createInjector(new TelegramBotModule());

        TelegramApi tApi = injector.getInstance(TelegramApi.class);

        UserDb userDb = injector.getInstance(UserDb.class);

        //injector.getInstance(FirstRunApp.class);

        CurrencyUpdateDailyService st = injector.getInstance(CurrencyUpdateDailyService.class);
        st.startAsync();

        Answer answer = injector.getInstance(Answer.class);
        while (true) {
            for (TelegramMessage message : tApi.update()) {
                userDb.add(message.getFrom().getId(), message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                tApi.sendAnswer(message.getFrom().getId(), answer.message(message.getText()));
            }
        }
    }
}
