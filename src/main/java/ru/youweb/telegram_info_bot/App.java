package ru.youweb.telegram_info_bot;

//@TODO Удалить неиспользуемый импорт

import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//@TODO Удалить ненужные комментарии(во всех классах)

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Config config = ConfigFactory.load();
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        TelegramApi tApi = new TelegramApi(config.getString("urlBot"), asyncHttpClient, new Gson());
        FixerApi fApi = new FixerApi(asyncHttpClient, new Gson());
        WorkDB workDB = new WorkDB(config.getString("db.jdbcUrl"), config.getString("db.user"), config.getString("db.pass"));

        FirstRunApp firstRunApp = new FirstRunApp(workDB, fApi);

        while (true) {
            for (TelegramMessage message : tApi.update()) {
                workDB.addUser(message.getFrom().getId(), message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                tApi.sendAnswer(message.getFrom().getId(), "text");
            }
        }
    }
}
