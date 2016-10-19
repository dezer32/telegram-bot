package ru.youweb.telegram_info_bot.db;

import com.google.inject.Inject;
import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QExchange;

import java.time.LocalDate;

import static ru.youweb.telegram_info_bot.PlainUtils.toSqlDate;

public class CurrencyRateDb {

    private static final QExchange e = QExchange.exchange;

    private SQLQueryFactory queryFactory;

    private CurrencyDb currencyDb;

    @Inject
    public CurrencyRateDb(SQLQueryFactory queryFactory, CurrencyDb currencyDb) {
        this.queryFactory = queryFactory;
        this.currencyDb = currencyDb;
    }

    public Double findValue(String curFrom, String curTo, LocalDate date) {
        return queryFactory
                .select(e.value)
                .from(e)
                .where(e.idCurFrom.eq(currencyDb.getId(curFrom)),
                        e.idCurTo.eq(currencyDb.getId(curTo)),
                        e.date.eq(toSqlDate(date)))
                .fetchOne();
    }

    public boolean isSetInfo(String currencyBase, LocalDate date) {
        return queryFactory.select(e.value).from(e)
                .where(e.idCurFrom.eq(currencyDb.getId(currencyBase)),
                        e.date.eq(toSqlDate(date)))
                .fetchCount() > 0;
    }

    public Integer add(String curFrom, String curTo, double value, LocalDate date) {
        if (findValue(curFrom, curTo, date) == null)
            return queryFactory.insert(e)
                    .set(e.idCurFrom, currencyDb.getId(curFrom))
                    .set(e.idCurTo, currencyDb.getId(curTo))
                    .set(e.value, value)
                    .set(e.date, toSqlDate(date))
                    .executeWithKey(Integer.class);
        else return null;
    }

    public void update(String curFrom, String curTo, double value, LocalDate date) {
        queryFactory.update(e)
                .where(e.idCurFrom.eq(currencyDb.getId(curFrom)),
                        e.idCurTo.eq(currencyDb.getId(curTo)),
                        e.date.eq(toSqlDate(date)))
                .set(e.value, value)
                .execute();
    }
}
