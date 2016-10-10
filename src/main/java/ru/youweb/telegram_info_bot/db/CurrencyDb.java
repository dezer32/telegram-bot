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

    public int getIdCur(String currency) {
        if (!currencyId.containsKey(currency)) {
            currencyId.put(currency, addCurrency(currency));
        }
        return currencyId.get(currency);
    }

    public List<String> getAllCurrency(){
        return new ArrayList<>(currencyId.keySet());
    }

    public int addCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;
        int findId = findCurrency(currency);
        if (findId == 0) {
            return queryFactory.insert(qCurrency).columns(qCurrency.nameCurrency).values(currency).executeWithKey(Integer.class);
        }
        return findId;
    }

    private int findCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;
        try {
            return queryFactory
                    .select(qCurrency.id)
                    .from(qCurrency)
                    .where(qCurrency.nameCurrency.eq(currency))
                    .fetchOne();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
