package ru.youweb.telegram_info_bot;

import com.querydsl.core.Tuple;
import com.querydsl.sql.*;
import com.zaxxer.hikari.HikariDataSource;
import ru.youweb.jdbc.QCurrency;
import ru.youweb.jdbc.QExchange;
import ru.youweb.jdbc.QUser;

import java.util.List;

public class WorkDB {

    private SQLQueryFactory queryFactory;

    public WorkDB(String jdbcUrl, String user, String pass) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(pass);

        SQLTemplates templates = new H2Templates();
        Configuration config = new Configuration(templates);
        queryFactory = new SQLQueryFactory(config, ds);
    }

    //Избавиться от нижнего метода
    private int getIdCur(String nameCurrency) {
        QCurrency qCurrency = QCurrency.currency;
        return queryFactory.select(qCurrency.id).from(qCurrency).where(qCurrency.nameCurrency.eq(nameCurrency)).fetchOne();
    }

    public List<Tuple> getIdCur() {
        QCurrency qCurrency = QCurrency.currency;
        return queryFactory.select(qCurrency.all()).from(qCurrency).fetch();
    }

    public void addUser(int id, String userName) {
        QUser qUser = QUser.user;
        long countUser = queryFactory
                .select(qUser.id)
                .from(qUser)
                .where(qUser.telegramId.eq(String.valueOf(id)))
                .fetchCount();
        if (countUser == 0)
            queryFactory.insert(qUser).columns(qUser.telegramId, qUser.name).values(id, userName).execute();
    }

    //delete method
    public double getAnswer(String curFrom, String curTo, String date) {
        QExchange qExchange = QExchange.exchange;

        return queryFactory
                .select(qExchange.value)
                .from(qExchange)
                .where(qExchange.idCurFrom.eq(getIdCur(curFrom))
                        .and(qExchange.idCurTo.eq(getIdCur(curTo)))
                        .and(qExchange.date.eq(date)))
                .fetchOne();
    }

    public double getAnswer(int curFrom, int curTo, String date) {
        QExchange qExchange = QExchange.exchange;

        return queryFactory
                .select(qExchange.value)
                .from(qExchange)
                .where(qExchange.idCurFrom.eq(curFrom)
                        .and(qExchange.idCurTo.eq(curTo))
                        .and(qExchange.date.eq(date)))
                .fetchOne();
    }

    public void addCurrencyRate(String curFrom, String curTo, double value, String date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.insert(qExchange)
                .columns(qExchange.idCurFrom, qExchange.idCurTo, qExchange.value, qExchange.date)
                .values(getIdCur(curFrom),
                        getIdCur(curTo),
                        value, date)
                .execute();
    }

    public void addCurrencyRate(int curFrom, int curTo, double value, String date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.insert(qExchange)
                .columns(qExchange.idCurFrom, qExchange.idCurTo, qExchange.value, qExchange.date)
                .values(curFrom,
                        curTo,
                        value, date)
                .execute();
    }

    public void updateCurrencyRate(String curFrom, String curTo, double value, String date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.update(qExchange)
                .where(qExchange.idCurFrom.eq(getIdCur(curFrom))
                        .and(qExchange.idCurTo.eq(getIdCur(curTo)))
                        .and(qExchange.date.eq(date)))
                .set(qExchange.value, value)
                .execute();
    }

    public void addCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;
        long count = queryFactory
                .select(qCurrency.id)
                .from(qCurrency)
                .where(qCurrency.nameCurrency.eq(currency))
                .fetchCount();
        if (count == 0) {
            queryFactory.insert(qCurrency).columns(qCurrency.nameCurrency).values(currency).execute();
        }
    }
}
