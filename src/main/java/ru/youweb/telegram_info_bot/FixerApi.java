package ru.youweb.telegram_info_bot;

import com.google.gson.Gson;
import org.asynchttpclient.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FixerApi {

    Request request;

    AsyncHttpClient asyncHttpClient;

    Gson gson;

    String urlFixer = "http://api.fixer.io";

    public FixerApi(AsyncHttpClient asyncHttpClient, Gson gson) {
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
    }

    public CurrencyRate getCurrencyRate(String currency, String date) throws ExecutionException, InterruptedException {
        request = new RequestBuilder()
        .setUrl(urlFixer + "/" + date)
        .addQueryParam("base", currency)
        .build();
        return asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<CurrencyRate>() {
            @Override
            public CurrencyRate onCompleted(Response response) throws Exception {
                return gson.fromJson(response.getResponseBody(), CurrencyRate.class);
            }
        }).get();
    }

}
