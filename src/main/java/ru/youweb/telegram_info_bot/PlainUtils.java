package ru.youweb.telegram_info_bot;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PlainUtils {

    public static Timestamp toTimestamp(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return java.sql.Date.valueOf(localDate);
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

}
