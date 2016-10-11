package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QUser;

public class UserDb {

    private SQLQueryFactory queryFactory;

    public UserDb(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    //@TODO переименовать в add или insert или create
    //@TODO параметр id переименовать в telegramId иначе путаешь с нашим id
    //@TODO в фабрике есть метод .merge которые делает вначале select а потом insert(или update) может использовать его вместо
    //@TODO select/insert
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
}
