package ru.youweb.telegram_info_bot;

import com.google.gson.Gson;
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
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

        Gson gson = new Gson();

        Config config = ConfigFactory.load();

        TelegramApi tApi = new TelegramApi(config.getString("urlBot"), asyncHttpClient, gson);

        FixerApi fApi = new FixerApi(asyncHttpClient, gson, config);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(config.getString("jdbcUrl"));
        ds.setUsername(config.getString("user"));
        ds.setPassword(config.getString("pass"));

        SQLTemplates templates = new H2Templates();
        Configuration configDb = new Configuration(templates);
        SQLQueryFactory queryFactory = new SQLQueryFactory(configDb, ds);

        UserDb userDb = new UserDb(queryFactory);
        CurrencyDb currencyDb = new CurrencyDb(queryFactory);
        CurrencyRateDb currencyRateDb = new CurrencyRateDb(queryFactory, currencyDb);

        new FirstRunApp(currencyRateDb, currencyDb, fApi, config);

        Timer timer = new Timer();
        SchedulerCurrencyUpdateTask st = new SchedulerCurrencyUpdateTask(fApi, currencyRateDb, currencyDb);

        LocalDate scheduleStart = LocalDate.parse(config.getString("timer.scheduleStart"));
        Duration duration = Duration.parse(config.getString("timer.schedulePeriod"));

        while (scheduleStart.isBefore(LocalDate.now()))
            scheduleStart.plusDays(duration.toDays());

        timer.schedule(st, new Date(scheduleStart.atStartOfDay().getSecond()), duration.toMillis());

        Answer answer = new Answer(currencyRateDb, currencyDb, config);
        while (true) {
            for (TelegramMessage message : tApi.update()) {
                userDb.add(message.getFrom().getId(), message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                tApi.sendAnswer(message.getFrom().getId(), answer.message(message.getText()));
            }
        }
    }
}
