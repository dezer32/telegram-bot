package ru.youweb.telegram_info_bot;

import com.github.racc.tscg.TypesafeConfigModule;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.youweb.telegram_info_bot.currency.FixerApi;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.db.UserDb;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;

public class TelegramBotModule extends AbstractModule {

    private Config config = ConfigFactory.load();

    @Override
    protected void configure() {
        install(TypesafeConfigModule.fromConfigWithPackage(config, "ru.youweb.telegram_info_bot"));
        bind(AsyncHttpClient.class).to(DefaultAsyncHttpClient.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("urlBot")).toInstance(config.getString("urlBot"));
        bind(Gson.class).in(Singleton.class);
        bind(CurrencyUpdateDailyService.class).in(Singleton.class);
        bind(TelegramApi.class).in(Singleton.class);
    }

    @Provides
    public Config provideConfig() {
        return config;
    }

    @Provides
    @Singleton
    public HikariDataSource provideHikariDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(config.getString("db.jdbcUrl"));
        ds.setUsername(config.getString("db.user"));
        ds.setPassword(config.getString("db.pass"));
        return ds;
    }

    @Provides
    @Singleton
    public SQLQueryFactory provideSQLQueryFactory() {
        SQLTemplates templates = new H2Templates();
        Configuration configDb = new Configuration(templates);
        return new SQLQueryFactory(configDb, provideHikariDataSource());
    }

    @Provides
    @Singleton
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder().cacheActive(true).build();
    }

    @Provides
    public Logger logger() {
        return LoggerFactory.getLogger(App.class);
    }

    @Provides
    public Flyway provideFlyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(provideHikariDataSource());
        flyway.migrate();
        return flyway;
    }

}
