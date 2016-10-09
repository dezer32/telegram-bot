package ru.youweb.telegram_info_bot.currency;

import com.google.gson.Gson;
import org.asynchttpclient.*;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;

import java.util.concurrent.ExecutionException;

//@TODO все поля пометить private
public class FixerApi {

    //@TODO Удалить Request отсюда
    Request request;

    AsyncHttpClient asyncHttpClient;

    Gson gson;

    String urlFixer = "http://api.fixer.io";

    public FixerApi(AsyncHttpClient asyncHttpClient, Gson gson) {
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
    }

    public CurrencyRate getCurrencyRate(String currency, String date) throws ExecutionException, InterruptedException {
        //@TODO Добавить Request сюда
        request = new RequestBuilder()
        .setUrl(urlFixer + "/" + date)
        .addQueryParam("base", currency)
        .build();

        System.out.println("load currency");

        return asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<CurrencyRate>() {
            @Override
            public CurrencyRate onCompleted(Response response) throws Exception {
                return gson.fromJson(response.getResponseBody(), CurrencyRate.class);
            }
        }).get();
    }

    public CurrencyRate getCurrencyRate(String currency) throws ExecutionException, InterruptedException {
        //@TODO Добавить Request сюда
        request = new RequestBuilder()
                .setUrl(urlFixer + "/latest")
                .addQueryParam("base", currency)
                .build();

        System.out.println("load currency");

        return asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<CurrencyRate>() {
            @Override
            public CurrencyRate onCompleted(Response response) throws Exception {
                return gson.fromJson(response.getResponseBody(), CurrencyRate.class);
            }
        }).get();
    }

}
