package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QExchange;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CurrencyRateDb {

    private SQLQueryFactory queryFactory;

    private CurrencyDb currencyDb;

    private DateTimeFormatter format;

    //@TODO Удалить DateTimeFormatter, даты в БД должны сохраняться в типе Date а не String
    public CurrencyRateDb(SQLQueryFactory queryFactory, CurrencyDb currencyDb, DateTimeFormatter format) {
        this.queryFactory = queryFactory;
        this.currencyDb = currencyDb;
        this.format = format;
    }

    public Double findValue(String curFrom, String curTo, LocalDate date) {
        QExchange qExchange = QExchange.exchange;
        return queryFactory
                .select(qExchange.value)
                .from(qExchange)
                .where(qExchange.idCurFrom.eq(currencyDb.getId(curFrom))
                        .and(qExchange.idCurTo.eq(currencyDb.getId(curTo)))
                        .and(qExchange.date.eq(new Date(date.atStartOfDay().getSecond()))))
                .fetchOne();
    }

    public Integer add(String curFrom, String curTo, double value, LocalDate date) {
        QExchange qExchange = QExchange.exchange;
         return queryFactory.insert(qExchange)
                .columns(qExchange.idCurFrom, qExchange.idCurTo, qExchange.value, qExchange.date)
                .values(currencyDb.getId(curFrom),
                        currencyDb.getId(curTo),
                        value, date.format(format))
                .executeWithKey(Integer.class);
    }

    public void update(String curFrom, String curTo, double value, LocalDate date) {
        QExchange qExchange = QExchange.exchange;

        queryFactory.update(qExchange)
                .where(qExchange.idCurFrom.eq(currencyDb.getId(curFrom))
                        .and(qExchange.idCurTo.eq(currencyDb.getId(curTo)))
                        .and(qExchange.date.eq(new Date(date.atStartOfDay().getSecond()))))
                .set(qExchange.value, value)
                .execute();
    }
}
