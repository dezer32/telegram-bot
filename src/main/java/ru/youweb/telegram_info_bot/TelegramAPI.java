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

public class TelegramAPI {

    private int updateId;
    private String urlBot;
    private TelegramGetUpdatesStruct updates;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson;

    private ArrayList<TelegramGetUpdatesStruct.TelegramMessageStruct> listUserMessage;

    public TelegramAPI() {
        urlBot = "https://api.telegram.org/bot105972211:AAGjTz3ZC48qTKEcvgkYso3FyeNf1VwsAKc";
        asyncHttpClient = new DefaultAsyncHttpClient();
        gson = new Gson();
        listUserMessage = new ArrayList<TelegramGetUpdatesStruct.TelegramMessageStruct>();
    }

    private boolean getUpdates() throws ExecutionException, InterruptedException {
        Future<Response> fResponse = asyncHttpClient.prepareGet(urlBot + "/getUpdates?offset=" + updateId).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                return response;
            }
        });

        Response response = fResponse.get();
        updates = gson.fromJson(response.getResponseBody(), TelegramGetUpdatesStruct.class);

        return updates.getValidateAnswer();
    }

    protected void sendAnswer(int id, String text) {
        asyncHttpClient.prepareGet(urlBot + "/sendMessage?chat_id=" + id + "&text=" + text).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                return null;
            }
        });
    }

    protected ArrayList<TelegramGetUpdatesStruct.TelegramMessageStruct> update() throws ExecutionException, InterruptedException {
        listUserMessage.clear();
        if (getUpdates())
        {
            for(TelegramGetUpdatesStruct.TelegramResultStruct result: updates.getResult())
            {
                listUserMessage.add(result.message);
            }
        }
        return listUserMessage;
    }
}
