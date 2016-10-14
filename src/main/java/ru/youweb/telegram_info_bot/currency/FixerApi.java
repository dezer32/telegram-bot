package ru.youweb.telegram_info_bot.currency;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.asynchttpclient.*;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyRate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class FixerApi {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private AsyncHttpClient asyncHttpClient;

    private Gson gson;

    private DateTimeFormatter format;

    private String urlFixer;

    @Inject
    public FixerApi(AsyncHttpClient asyncHttpClient, Gson gson, Config config) {
        this.asyncHttpClient = asyncHttpClient;
        this.gson = gson;
        this.format = DateTimeFormatter.ofPattern(config.hasPath("dateFormatApi") ? config.getString("dateFormatApi") : DEFAULT_DATE_FORMAT);
        this.urlFixer = config.getString("urlFixerApi");
    }

    public CurrencyRate getCurrencyRate(String currency, LocalDate date) throws ExecutionException, InterruptedException {
        return getCurrencyRate(currency, date.format(format));
    }

    public CurrencyRate getCurrencyRate(String currency) throws ExecutionException, InterruptedException {
        return getCurrencyRate(currency, "latest");
    }

    private CurrencyRate getCurrencyRate(String currency, String date) throws ExecutionException, InterruptedException {
        Request request = new RequestBuilder()
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
