package ru.youweb.telegram_info_bot.currency;

import com.querydsl.core.Tuple;
import ru.youweb.jdbc.QCurrency;
import ru.youweb.telegram_info_bot.WorkDB;
import ru.youweb.telegram_info_bot.currency.dto.CurrencyId;

import java.util.ArrayList;
import java.util.List;

public class AllCurrencyId {

    private List<CurrencyId> currencyIdList = new ArrayList<CurrencyId>();

    private WorkDB workDB;

    public AllCurrencyId(WorkDB workDB) {
        this.workDB = workDB;
        load();
    }

    public void load(){
        currencyIdList.clear();
        List<Tuple> qCurrencies = workDB.getIdCur();
        QCurrency qCurrency = QCurrency.currency;
        for (Tuple item : qCurrencies) {
            currencyIdList.add(new CurrencyId(new Integer(item.get(qCurrency.id)), item.get(qCurrency.nameCurrency)));
        }
    }

    public int getId(String nameCur) {
        for (CurrencyId item : currencyIdList) {
            if (item.getNameCurrency().equals(nameCur)) {
                return item.getId();
            }
        }
        workDB.addCurrency(nameCur);
        load();
        return getId(nameCur);
    }
}
