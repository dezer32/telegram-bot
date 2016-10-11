package ru.youweb.telegram_info_bot.currency;

import com.google.gson.Gson;
import org.asynchttpclient.*;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class FixerApi {

    //@TODO Добавь
    // private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private AsyncHttpClient asyncHttpClient;

    private Gson gson;

    //@TODO добавь private
    DateTimeFormatter format;

    //@TODO Вынести в конфиг
    private String urlFixer = "http://api.fixer.io";

    //@TODO Передавать Config как 3й параметр
    /*
        public FixerApi(AsyncHttpClient asyncHttpClient, Gson gson, Config config) {
            this.asyncHttpClient = asyncHttpClient;
            this.gson = gson;
            this.format = DateTimeFormat.ofPattern(config.hasPath(...) ? config.getString(...) : DEFAULT_DATE_FORMAT;
        }
     */
    public FixerApi(AsyncHttpClient asyncHttpClient, Gson gson, DateTimeFormatter format) {
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
        this.format = format;
    }

    //@TODO Методы getCurrencyRate отличаются одной строкой
    //@TODO Отрефакторить код таким образом, чтобы повторяемости не было, тут нужно добавить третий приватный метод
    //@TODO куда перенести код из этих двух. Сделать ему сигнатуру getCurrencyRate(String currency, String date)
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
