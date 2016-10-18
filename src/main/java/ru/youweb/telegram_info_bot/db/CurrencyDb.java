package ru.youweb.telegram_info_bot.db;

import com.google.inject.Inject;
import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QCurrency;

import java.util.*;

public class CurrencyDb {

    private static final QCurrency qc = QCurrency.currency;

    private SQLQueryFactory queryFactory;

    private Map<String, Integer> currencyId = new HashMap<>();

    @Inject
    public CurrencyDb(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
        loadCurrency();
    }

    public int getId(String currency) {
        Integer id = currencyId.get(currency);
        if (id == null) {
            id = add(currency);
            currencyId.put(currency, id);
        }
        return id;
    }

    public List<String> getAllCurrencies() {
        return new ArrayList<>(currencyId.keySet());
    }

    private Integer add(String currency) {
        Integer id = findCurrencyId(currency);
        if (id == null) {
            id = insert(currency);
        }
        return id;
    }

    private Integer insert(String currency) {
        return queryFactory.insert(qc).set(qc.nameCurrency, currency).executeWithKey(Integer.class);
    }

    private Integer findCurrencyId(String currency) {
        return queryFactory.select(qc.id).from(qc).where(qc.nameCurrency.eq(currency)).fetchOne();
    }

    private void loadCurrency() {
        queryFactory.select(qc.nameCurrency).from(qc).fetch().forEach(this::getId);
    }
}
