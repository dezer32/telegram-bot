package ru.youweb.telegram_info_bot.db;

import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QUser;

public class UserDb {

    private SQLQueryFactory queryFactory;

    public UserDb(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public void add(int telegramId, String userName) {
        QUser qUser = QUser.user;
        if (findUser(telegramId))
            queryFactory.insert(qUser).columns(qUser.telegramId, qUser.name).values(telegramId, userName).execute();
    }

    private boolean findUser(Integer telegramId) {
        QUser qUser = QUser.user;
        return queryFactory
                .select(qUser.id)
                .from(qUser)
                .where(qUser.telegramId.eq(String.valueOf(telegramId)))
                .fetchCount() == 0;
    }
}
