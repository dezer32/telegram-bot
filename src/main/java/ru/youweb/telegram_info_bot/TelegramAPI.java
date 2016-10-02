package ru.youweb.telegram_info_bot;


import com.google.gson.Gson;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//@TODO Переместить класс в паке ru.youweb.telegram_info_bot.telegram
public class TelegramAPI {

    //@TODO Откомментировать все поля(зачем они нужны в этом классе)
    private int updateId;
    private String urlBot;
    private TelegramGetUpdatesStruct updates;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson;

    private ArrayList<TelegramGetUpdatesStruct.TelegramMessageStruct> listUserMessage;

    public TelegramAPI() {
        //@TODO Перенести константу в конфигурационный файл(Typesafe Config)
        urlBot = "https://api.telegram.org/bot105972211:AAGjTz3ZC48qTKEcvgkYso3FyeNf1VwsAKc";
        //@TODO Клиент создавать выше передавать сюда как параметр
        asyncHttpClient = new DefaultAsyncHttpClient();
        //@TODO GSON создавать выше, передавать сюда как параметр
        gson = new Gson();
        listUserMessage = new ArrayList<TelegramGetUpdatesStruct.TelegramMessageStruct>();
    }

    //@TODO упростить тело метода и возвращать результаты полученные от Telegram с помощью return
    private boolean getUpdates() throws ExecutionException, InterruptedException {
        //@TODO параметры в HTTP запрос передавать не с помощью строки а с помощью средств AsyncHttpClient(предварительно прочитать как)
        Future<Response> fResponse = asyncHttpClient.prepareGet(urlBot + "/getUpdates?offset=" + updateId).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                return response;
            }
        });

        Response response = fResponse.get();
        //@TODO Перенести парсинг в метод onCompleted
        updates = gson.fromJson(response.getResponseBody(), TelegramGetUpdatesStruct.class);

        //@TODO Перенести эту проверку в onCompleted, если проверка не прошла выбрасывать исключение(можно сделать собственное исключение ApplicationException)
        return updates.getValidateAnswer();
    }

    //@TODO если метод не нужно использовать в будущем сделать его private, если нужно использовать во внешней среде, сделать public
    //@TODO написать комментарий на метод в стиле JavaDoc(почитать что это)
    protected void sendAnswer(int id, String text) {
        //@TODO Для операций добавления использовать POST запрос
        asyncHttpClient.prepareGet(urlBot + "/sendMessage?chat_id=" + id + "&text=" + text).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                //@TODO если не предполагается ничего обрабатывать то лучше избавиться от этого обработчика
                return null;
            }
        });
    }

    //@TODO написать комментарий на метод в стиле JavaDoc(почитать что это)
    protected ArrayList<TelegramGetUpdatesStruct.TelegramMessageStruct> update() throws ExecutionException, InterruptedException {
        listUserMessage.clear();
        if (getUpdates())
        {
            for(TelegramGetUpdatesStruct.TelegramResultStruct result: updates.getResult())
            //@TODO фигурная скобка в конце строки а не в начале
            {
                listUserMessage.add(result.message);
                if (result.update_id >= updateId)
                    updateId = result.update_id + 1;
            }
        }
        return listUserMessage;
    }
}
