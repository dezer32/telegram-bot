package ru.youweb.telegram_info_bot;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

        Gson gson = new Gson();

        Config config = ConfigFactory.load();

        WorkDB workDB = new WorkDB(config.getConfig("db"));

        TelegramApi tApi = new TelegramApi(config.getString("urlBot"), asyncHttpClient, gson);

        FixerApi fApi = new FixerApi(asyncHttpClient, gson, DateTimeFormatter.ofPattern(config.getString("dateFormatApi")));

        new FirstRunApp(workDB, fApi, config);

        Timer timer = new Timer();

        SchedulerTask st = new SchedulerTask(fApi, workDB, DateTimeFormatter.ofPattern(config.getString("db.dateFromat")));

        long timeScheduleStart = config.getLong("timer.scheduleStart");
        long timeSchedulePeriod = config.getLong("timer.schedulePeriod");

        while (timeScheduleStart < System.currentTimeMillis())
            timeScheduleStart += timeSchedulePeriod;

        timer.schedule(st, new Date(timeScheduleStart), timeSchedulePeriod);

        Answer answer = new Answer(workDB);

        while (true) {
            for (TelegramMessage message : tApi.update()) {
                workDB.addUser(message.getFrom().getId(), message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                tApi.sendAnswer(message.getFrom().getId(), answer.message(message.getText()));
            }
        }
    }
}
