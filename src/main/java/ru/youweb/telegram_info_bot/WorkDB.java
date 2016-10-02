package ru.youweb.telegram_info_bot;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.zaxxer.hikari.HikariDataSource;
import ru.youweb.jdbc.QUser;

//@TODO Удалить неиспользуемый комментарий
/**
 * Created by Youweb on 30.09.2016.
 */
public class WorkDB {

    private SQLQueryFactory queryFactory;

    public WorkDB() {
        HikariDataSource ds = new HikariDataSource();
        //@TODO все настройки вынести в конфиг Typesafe Config, как работать с библиотекой прочитать в офф документации
        ds.setJdbcUrl("jdbc:h2:./tbot");
        ds.setUsername("youweb");
        ds.setPassword("");

        //@TODO SQLQueryFactory эту фабрику передавать как параметр а не создавать тут, потому что фабрика может понадобиться в других подобных классах
        SQLTemplates templates = new H2Templates();
        Configuration config = new Configuration(templates);
        queryFactory = new SQLQueryFactory(config, ds);
    }

    public void addUser(int id, String userName) {
        QUser qUser = QUser.user;
        //@TODO qUser.all() заменить на selectFrom(qUser)
        long countUser = queryFactory.select(qUser.all()).from(qUser).where(qUser.telegramId.eq(String.valueOf(id))).fetchCount();
        if (countUser == 0)
            queryFactory.insert(qUser).columns(qUser.telegramId, qUser.name).values(id, userName).execute();
    }
}
