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

    //@TODO getIdCur плохо читается, либо сократи до getId либо увеличь до getCurrencyId
    public int getIdCur(String currency) {
        Integer id = currencyId.get(currency);
        //Лучше не вызывать .containsKey потому что он делает почти тоже самое что и .get и получится что ты
        //сделаешь поиск два раза
        if (id == null) {
            id = addCurrency(currency);
            currencyId.put(currency, id);
        }
        return id;
    }

    //@TODO В этом методе есть потенциальные баг, мы можем добавить записи с помощью метода
    //@TODO addCurrency а этот метод продолжит возвращать пустой список
    public List<String> getAllCurrency() {
        return new ArrayList<>(currencyId.keySet());
    }

    //@TODO Переименуй в add
    public Integer addCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;

        Integer id = findCurrency(currency);
        if (id == null) {
            id = queryFactory.insert(qCurrency).columns(qCurrency.nameCurrency).values(currency).executeWithKey(Integer.class);
        }
        return id;
    }

    //@TODO Переименуй в findCurrencyId
    /*
    В качестве возвращаемого значения лучше использовать Integer, почитай что такое классы обертки и в чем их отличие от примитивов
    //@TODO Удали этот комментарий
     */
    private Integer findCurrency(String currency) {
        QCurrency qCurrency = QCurrency.currency;

        return queryFactory.select(qCurrency.id).from(qCurrency).where(qCurrency.nameCurrency.eq(currency)).fetchOne();
    }
}
