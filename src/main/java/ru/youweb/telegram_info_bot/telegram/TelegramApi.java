package ru.youweb.telegram_info_bot.telegram;


import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramGetUpdates;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramMessage;
import ru.youweb.telegram_info_bot.telegram.dto.TelegramResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TelegramApi {

    /**
     * ID сообщения, используется для дальнейшего запрета на получение данного сообщения
     */
    private int updateId;

    /**
     * Адресс Телеграм бота
     */
    private String urlBot;

    private AsyncHttpClient asyncHttpClient;

    private Gson gson;

    private Logger log;

    @Inject
    public TelegramApi(@Named("urlBot") String urlBot, AsyncHttpClient asyncHttpClient, Gson gson, Logger log) {
        this.urlBot = urlBot;
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
        this.log = log;
    }

    /**
     * Отправляет сообщение в чат Telegram
     *
     * @param id   идентификатор чата с пользователем
     * @param text текст ответа
     */
    public void sendAnswer(int id, String text) {
        Request request = new RequestBuilder()
                .setUrl(urlBot + "sendMessage")
                .addFormParam("chat_id", String.valueOf(id))
                .addFormParam("text", String.valueOf(text))
                .addQueryParam("parse_mode", "HTML")
                .build();

        asyncHttpClient.executeRequest(request);

        log.info("Отправка ответа: ID={}", id);
    }

    /**
     * Запрашивает новые сообщения, парсит их и заносит в список listUserMessage
     *
     * @return возвращает список сообщение пользователей
     */
    public List<TelegramMessage> update() throws ExecutionException, InterruptedException {
        List<TelegramMessage> listUserMessage = new ArrayList<>();
        for (TelegramResult result : getUpdates().getResult()) {
            listUserMessage.add(result.getMessage());
            if (result.getUpdateId() >= updateId)
                updateId = result.getUpdateId() + 1;
        }
        if (listUserMessage.size() > 0)
            log.info("Получили сообщения ({})", listUserMessage.size());
        return listUserMessage;
    }

    private TelegramGetUpdates getUpdates() throws ExecutionException, InterruptedException {
        Request request = new RequestBuilder()
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
}
