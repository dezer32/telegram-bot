package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QExchange;

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

    //@TODO Класс для работы с БД ничего не должен знать о бизнес логике, его задача выдывать и добавлять данные в БД,
    //@TODO лучше переименовать этот метод в getValue или findValue
    //@TODO возвращаемое значение сделай Double иначе если .fetchOne вернет null у тебя возникнет NullPointerException
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

    //@TODO Название addCurrencyRate в классе CurrencyRateDb избыточно, сократи до add или insert или create
    //@TODO обычно такие методы возвращают сгенеренный ID
    //@TODO для этого можно использовать метод executeWithKey(qExchange.id)
    public void addCurrencyRate(String curFrom, String curTo, double value, LocalDate date) {
        QExchange qExchange = QExchange.exchange;
        queryFactory.insert(qExchange)
                .columns(qExchange.idCurFrom, qExchange.idCurTo, qExchange.value, qExchange.date)
                .values(currencyDb.getIdCur(curFrom),
                        currencyDb.getIdCur(curTo),
                        value, date.format(format))
                .execute();
    }

    //@TODO Тоже самое, сократить название до update
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
