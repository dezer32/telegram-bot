package ru.youweb.telegram_info_bot;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.db.UserDb;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;

public class TelegramBotModule extends AbstractModule {
    private Config config = ConfigFactory.load();

    @Override
    protected void configure() {
        bind(AsyncHttpClient.class).to(DefaultAsyncHttpClient.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("urlBot")).toInstance(config.getString("urlBot"));
        bind(Gson.class).in(Singleton.class);
        bind(SQLTemplates.class).to(H2Templates.class);
    }

    @Provides
    Config provideConfig() {
        return config;
    }

    @Provides @Singleton
    HikariDataSource provideHikariDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(config.getString("db.jdbcUrl"));
        ds.setUsername(config.getString("db.user"));
        ds.setPassword(config.getString("db.pass"));
        return ds;
    }

    @Provides @Singleton
    SQLQueryFactory provideSQLQueryFactory() {
        SQLTemplates templates = new H2Templates();
        Configuration configDb = new Configuration(templates);
        SQLQueryFactory queryFactory = new SQLQueryFactory(configDb, provideHikariDataSource());
        return queryFactory;
    }

    @Provides
    LocalDateTime provideLocalDateTime() {
        LocalDate scheduleDateStart = LocalDate.parse(config.getString("timer.scheduleStart"), DateTimeFormatter.ofPattern(config.getString("timer.format")));
        return LocalDateTime.from(scheduleDateStart.atStartOfDay()).plusHours(config.getLong("timer.scheduleTimeStartAM"));
    }

    @Provides
    Duration provideDuration() {
        return Duration.parse(config.getString("timer.schedulePeriod"));
    }
}
