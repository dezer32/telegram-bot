package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QExchange;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CurrencyRateDb {

    private SQLQueryFactory queryFactory;

    private CurrencyDb currencyDb;

    private DateTimeFormatter format;

    public CurrencyRateDb(SQLQueryFactory queryFactory, CurrencyDb currencyDb, DateTimeFormatter format) {
        this.queryFactory = queryFactory;
        this.currencyDb = currencyDb;
        this.format = format;
    }

    public double getAnswer(String curFrom, String curTo, String date) {
        QExchange qExchange = QExchange.exchange;

        return queryFactory
                .select(qExchange.value)
                .from(qExchange)
                .where(qExchange.idCurFrom.eq(currencyDb.getIdCur(curFrom))
                        .and(qExchange.idCurTo.eq(currencyDb.getIdCur(curTo)))
                        .and(qExchange.date.eq(date)))
                .fetchOne();
    }

    public void addCurrencyRate(String curFrom, String curTo, double value, LocalDate date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.insert(qExchange)
                .columns(qExchange.idCurFrom, qExchange.idCurTo, qExchange.value, qExchange.date)
                .values(currencyDb.getIdCur(curFrom),
                        currencyDb.getIdCur(curTo),
                        value, date.format(format))
                .execute();
    }

    public void updateCurrencyRate(String curFrom, String curTo, double value, String date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.update(qExchange)
                .where(qExchange.idCurFrom.eq(currencyDb.getIdCur(curFrom))
                        .and(qExchange.idCurTo.eq(currencyDb.getIdCur(curTo)))
                        .and(qExchange.date.eq(date)))
                .set(qExchange.value, value)
                .execute();
    }
}
