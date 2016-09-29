package ru.youweb.telegram_info_bot;

import com.google.gson.Gson;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.zaxxer.hikari.HikariDataSource;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import ru.youweb.jdbc.QCurrency;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:./tbot");
        ds.setUsername("youweb");
        ds.setPassword("");

        SQLTemplates template = new H2Templates();
        Configuration config = new Configuration(template);
        SQLQueryFactory queryFactory = new SQLQueryFactory(config, ds);

        Gson gson = new Gson();

        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

        Future<Response> answer = asyncHttpClient.prepareGet("http://api.fixer.io/latest").execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                return response;
            }
        });

        Response response = answer.get();
        System.out.println(response.getResponseBody());
        CurrencyRateStruct currencyRate = gson.fromJson(response.getResponseBody(), CurrencyRateStruct.class);



        QCurrency currency = QCurrency.currency;

        queryFactory.insert(currency).columns(currency.nameCurrency).values(currencyRate.getBase()).execute();
        System.out.println("enter base");

        for (Map.Entry<String, Double> item: currencyRate.getRates().entrySet()) {
            queryFactory.insert(currency).columns(currency.nameCurrency).values(item.getKey()).execute();

            System.out.println("Enter " + item.getKey() + " : " + item.getValue());
        }
        System.out.println("Ales kaput");
    }
}
