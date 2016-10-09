package ru.youweb.telegram_info_bot.telegram;


import com.google.gson.Gson;
import org.asynchttpclient.*;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramGetUpdates;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramResult;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TelegramApi {

    //@TODO Комментарии писать в стиле JavaDoc
    /**
     * Комментарий
     */

    //ID сообщения, используется для дальнейшего запрета на получение данного сообщения
    private int updateId;

    //Адресс Телеграм бота
    private String urlBot;

    //@TODO Удалить это поле, оно не используется
    //Объект с новыми сообщениями телеграмм боту
    private TelegramGetUpdates updates;

    //@TODO Переместить listUserMessage на уровень метода где этот список используется
    //Список с сообщениеми
    private ArrayList<TelegramMessage> listUserMessage;

    //@TODO Это поле не используется его нужно удалить
    private ArrayList<Param> params;

    private AsyncHttpClient asyncHttpClient;

    private Gson gson;

    //@TODO Удалить объявление Request отсюда
    private Request request;

    public TelegramApi(String urlBot, AsyncHttpClient asyncHttpClient, Gson gson) {
        this.urlBot = urlBot;
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
        listUserMessage = new ArrayList<TelegramMessage>();
        params = new ArrayList<Param>();
    }

    //@TODO Публичные методы всегда объявляются в классе выше чем приватные, этот метод нужно переместить вниз
    private TelegramGetUpdates getUpdates() throws ExecutionException, InterruptedException {
        //@TODO Объявление Request переместить сюда(чем уже область видимости тем лучше)
        request = new RequestBuilder()
                .setUrl(urlBot + "getUpdates")
                .addQueryParam("offset", String.valueOf(updateId))
                .addQueryParam("timeout", "7")
                .setRequestTimeout(10000)
                .build();

        return asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<TelegramGetUpdates>() {
            @Override
            public TelegramGetUpdates onCompleted(Response response) throws Exception {
                return gson.fromJson(response.getResponseBody(), TelegramGetUpdates.class);
            }
        }).get();
    }

    /**
     * //@TODO Тут давать краткий комментарий о том что делает метод
     *
     * @param id   идентификатор чата с пользователем
     * @param text текст ответа
     */
    public void sendAnswer(int id, String text) {
        request = new RequestBuilder()
                .setUrl(urlBot + "sendMessage")
                .addFormParam("chat_id", String.valueOf(id))
                .addFormParam("text", String.valueOf(text))
                .build();

        asyncHttpClient.executeRequest(request);
    }

    /**
     * //@TODO Тут давать краткий комментарий о том что делает метод
     *
     * @return возвращает список сообщение пользователей
     */
    public ArrayList<TelegramMessage> update() throws ExecutionException, InterruptedException {
        //@TODO listUserMessage создавать тут
        //List<TelegramMessage> messages = new ArraysList<>()
        listUserMessage.clear();
        for (TelegramResult result : getUpdates().getResult()) {
            listUserMessage.add(result.getMessage());
            if (result.getUpdateId() >= updateId)
                updateId = result.getUpdateId() + 1;
        }
        return listUserMessage;
    }
}
