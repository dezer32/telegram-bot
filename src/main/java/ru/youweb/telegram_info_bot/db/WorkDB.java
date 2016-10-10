package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.*;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariDataSource;
import ru.youweb.jdbc.QCurrency;
import ru.youweb.jdbc.QExchange;
import ru.youweb.jdbc.QUser;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyId;
import ru.youweb.telegram_info_bot.db.CurrencyDb;
import ru.youweb.telegram_info_bot.db.CurrencyRateDb;
import ru.youweb.telegram_info_bot.db.UserDb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//В этих классах сделать методы insert/update/delete/find
public class WorkDB {

    private SQLQueryFactory queryFactory;

    private UserDb userDb;

    private CurrencyDb currencyDb;

    private CurrencyRateDb currencyRateDb;

    public WorkDB(Config confAppDb) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(confAppDb.getString("jdbcUrl"));
        ds.setUsername(confAppDb.getString("user"));
        ds.setPassword(confAppDb.getString("pass"));

        SQLTemplates templates = new H2Templates();
        Configuration configDb = new Configuration(templates);
        queryFactory = new SQLQueryFactory(configDb, ds);
        queryFactory = new SQLQueryFactory(configDb, ds);
        queryFactory = new SQLQueryFactory(configDb, ds);

        userDb = new UserDb(queryFactory);
        currencyDb = new CurrencyDb(queryFactory);
        currencyRateDb = new CurrencyRateDb(queryFactory, currencyDb, DateTimeFormatter.ofPattern(confAppDb.getString("dateFromat")));
    }

    public UserDb UserDb() {
        return userDb;
    }

    public CurrencyDb CurrencyDb() {
        return currencyDb;
    }

    public CurrencyRateDb CurrencyRateDb() {
        return currencyRateDb;
    }
}
