package ru.youweb.telegram_info_bot.db;

import com.google.inject.Inject;
import com.querydsl.sql.SQLQueryFactory;
import ru.youweb.jdbc.QUser;

public class UserDb {

    private static final QUser u = QUser.user;

    private SQLQueryFactory queryFactory;

    @Inject
    public UserDb(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public void add(int telegramId, String userName) {
        if (findUserId(telegramId) == null) {
            insert(telegramId, userName);
        }
    }

    private Integer insert(int telegramId, String userName) {
        return queryFactory.insert(u).set(u.telegramId, telegramId).set(u.name, userName).executeWithKey(u.id);
    }

    private Integer findUserId(int telegramId) {
        return queryFactory.select(u.id).from(u).where(u.telegramId.eq(telegramId)).fetchOne();
    }
}
