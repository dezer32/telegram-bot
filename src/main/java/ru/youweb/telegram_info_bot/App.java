package ru.youweb.telegram_info_bot;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TelegramAPI tApi = new TelegramAPI();
        WorkDB workDB = new WorkDB();
        con("начало цикла");
        for (TelegramGetUpdatesStruct.TelegramMessageStruct message : tApi.update()) {
            workDB.addUser(message.from.id, message.from.first_name + " " + message.from.last_name);
            con("Добавлено в бд юзер");
            tApi.sendAnswer(message.from.id, "мегОтвет");
            con("отправден ответ");
        }
    }

    public static void con(String str) {
        System.out.println(str);
    }

}
