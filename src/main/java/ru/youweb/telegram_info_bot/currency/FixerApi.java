package ru.youweb.telegram_info_bot.currency;

import com.google.gson.Gson;
import org.asynchttpclient.*;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class FixerApi {

    private AsyncHttpClient asyncHttpClient;

    private Gson gson;

    DateTimeFormatter format;

    private String urlFixer = "http://api.fixer.io";

    public FixerApi(AsyncHttpClient asyncHttpClient, Gson gson, DateTimeFormatter format) {
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
        this.format = format;
    }

    public CurrencyRate getCurrencyRate(String currency, LocalDate date) throws ExecutionException, InterruptedException {
        Request request = new RequestBuilder()
                .setUrl(urlFixer + "/" + date.format(format))
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
        Request request = new RequestBuilder()
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
