package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QCurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyDb {

    private SQLQueryFactory queryFactory;

    private Map<String, Integer> currencyId = new HashMap<String, Integer>();

    public CurrencyDb(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public int getId(String currency) {
        Integer id = currencyId.get(currency);
        //Лучше не вызывать .containsKey потому что он делает почти тоже самое что и .get и получится что ты
        //сделаешь поиск два раза
        if (id == null) {
            id = add(currency);
            currencyId.put(currency, id);
        }
        return id;
    }
    public List<String> getAllCurrency() {
        return new ArrayList<>(currencyId.keySet());
    }

    private Integer add(String currency) {
        QCurrency qCurrency = QCurrency.currency;

        Integer id = findCurrencyId(currency);
        if (id == null) {
            id = queryFactory.insert(qCurrency).columns(qCurrency.nameCurrency).values(currency).executeWithKey(Integer.class);
        }
        return id;
    }

    private Integer findCurrencyId(String currency) {
        QCurrency qCurrency = QCurrency.currency;

        return queryFactory.select(qCurrency.id).from(qCurrency).where(qCurrency.nameCurrency.eq(currency)).fetchOne();
    }
}
