package ru.youweb.telegram_info_bot;

import com.querydsl.sql.*;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariDataSource;
import ru.youweb.jdbc.QCurrency;
import ru.youweb.jdbc.QExchange;
import ru.youweb.jdbc.QUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@TODO Удалить неиспользуемые методы
//@TODO Этот класс слишком сложный, это подтверждается наличием методов со сложными названиями (addUser, addCurrencyRate),
//нужно разбить его на несколько более мелких классов которые будут заниматься только своей работой, например
//UserDb(или UserDao, UserDbHelper, или любое другое), CurrencyDb и т.д.
//В этих классах сделать методы insert/update/delete/find
//Дополнительные классы поместить в пакет ru.youweb.telegram_info_bot.db
public class WorkDB {

    private SQLQueryFactory queryFactory;

    private Map<String, Integer> currencyId = new HashMap<String, Integer>();

    private Config confAppDb;

    public WorkDB(Config confAppDb) {
        this.confAppDb = confAppDb;
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(confAppDb.getString("jdbcUrl"));
        ds.setUsername(confAppDb.getString("user"));
        ds.setPassword(confAppDb.getString("pass"));

        SQLTemplates templates = new H2Templates();
        Configuration configDb = new Configuration(templates);
        queryFactory = new SQLQueryFactory(configDb, ds);
        queryFactory = new SQLQueryFactory(configDb, ds);
        queryFactory = new SQLQueryFactory(configDb, ds);
    }

    /**
     * @TODO
     * Методы DB не должны возвращать объект Tuple. Это внутреннее представление строки из таблицы БД.
     * В этом методе нужно использовать select(Projections.constructor(Clazz.class, <поля>)
     *
     * @return
     */

    private int getIdCur(String currency) {
        if (!currencyId.containsKey(currency)) {
            currencyId.put(currency, addCurrency(currency));
        }
        return currencyId.get(currency);
    }

    public List<String> getAllCurrency(){
        return currencyId.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
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

    public void addCurrencyRate(String curFrom, String curTo, double value, LocalDate date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.insert(qExchange)
                .columns(qExchange.idCurFrom, qExchange.idCurTo, qExchange.value, qExchange.date)
                .values(getIdCur(curFrom),
                        getIdCur(curTo),
                        value, date.format(DateTimeFormatter.ofPattern(confAppDb.getString("dateFromat"))))
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

    public int addCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;
        int findId = findCurrency(currency);
        if (findId > 0) {
            return queryFactory.insert(qCurrency).columns(qCurrency.nameCurrency).values(currency).executeWithKey(Integer.class);
        }
        return findId;
    }

    private int findCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;

        return queryFactory
                .select(qCurrency.id)
                .from(qCurrency)
                .where(qCurrency.nameCurrency.eq(currency))
                .fetchOne();
    }
}
