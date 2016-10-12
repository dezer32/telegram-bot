package ru.youweb.telegram_info_bot;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.db.UserDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Injector injector = Guice.createInjector(new TelegramBotModule());

        TelegramApi tApi = injector.getInstance(TelegramApi.class);

        UserDb userDb = injector.getInstance(UserDb.class);

        //injector.getInstance(FirstRunApp.class);

        SchedulerCurrencyUpdateTask st = injector.getInstance(SchedulerCurrencyUpdateTask.class);

        LocalDateTime scheduleStart = injector.getInstance(LocalDateTime.class);

        Duration duration = injector.getInstance(Duration.class);

        while (scheduleStart.isBefore(LocalDateTime.now()))
            scheduleStart = scheduleStart.plusHours(duration.toHours());

        Timer timer = new Timer();

        timer.schedule(st, new Date(scheduleStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()), duration.toMillis());

        Answer answer = injector.getInstance(Answer.class);
        while (true) {
            for (TelegramMessage message : tApi.update()) {
                userDb.add(message.getFrom().getId(), message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                tApi.sendAnswer(message.getFrom().getId(), answer.message(message.getText()));
            }
        }
    }
}
