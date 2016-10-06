package ru.youweb.telegram_info_bot;

//@TODO Удалить неиспользуемый импорт

import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import ru.youweb.telegram_info_bot.currency.AllCurrencyId;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

//@TODO Удалить ненужные комментарии(во всех классах)

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

        Gson gson = new Gson();

        Config config = ConfigFactory.load();

        WorkDB workDB = new WorkDB(config.getString("db.jdbcUrl"), config.getString("db.user"), config.getString("db.pass"));

        TelegramApi tApi = new TelegramApi(config.getString("urlBot"), asyncHttpClient, gson);

        FixerApi fApi = new FixerApi(asyncHttpClient, gson);

        AllCurrencyId currencyId = new AllCurrencyId(workDB);

        FirstRunApp firstRunApp = new FirstRunApp(workDB, fApi, currencyId);

        Timer timer = new Timer();


        SchedulerTask st = new SchedulerTask(fApi, workDB, currencyId);

        long timeScheduleStart = config.getLong("timer.scheduleStart");
        long timeSchedulePeriod = config.getLong("timer.schedulePeriod");

        while (timeScheduleStart < System.currentTimeMillis())
            timeScheduleStart += timeSchedulePeriod;

        timer.schedule(st, new Date(timeScheduleStart), timeSchedulePeriod);

        Answer answer = new Answer(workDB, currencyId);

        while (true) {
            for (TelegramMessage message : tApi.update()) {
                workDB.addUser(message.getFrom().getId(), message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                tApi.sendAnswer(message.getFrom().getId(), answer.message(message.getText()));
            }
        }
    }
}
