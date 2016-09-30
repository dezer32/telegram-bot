package ru.youweb.telegram_info_bot;

import java.util.List;

/**
 * Created by Youweb on 30.09.2016.
 */
public class TelegramGetUpdatesStruct {

    public class TelegramAuthorInfoStruct {
        protected int id;
        protected String first_name;
        protected String last_name;
    }

    public class TelegramMessageStruct {
        protected int message_id;
        protected int date;
        protected String text;
        protected TelegramAuthorInfoStruct from;
    }

    public class TelegramResultStruct {
        protected int update_id;
        protected TelegramMessageStruct message;
    }

    private boolean ok;
    private List<TelegramResultStruct> result;

    public TelegramGetUpdatesStruct() {

    }

    public List<TelegramResultStruct> getResult() {
        return result;
    }

    public boolean getValidateAnswer() {
        if (ok == true && !result.isEmpty())
            return true;
        return false;
    }

}

