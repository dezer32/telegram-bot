package ru.youweb.telegram_info_bot;

import java.util.List;

//@TODO Удалить неиспользуемый комментарий
/**
 * Created by Youweb on 30.09.2016.
 */
//@TODO Удалить Struct
public class TelegramGetUpdatesStruct {

    //@TODO Все классы перенести в пакет ru.youweb.telegram_info_bot.telegram.dto(предварительно создать пакет)
    //@TODO Все внутренние классы вынести на верхний уровень
    public class TelegramAuthorInfoStruct {
        protected int id;
        //@TODO Названия полей в camelCase(firstName), для парсинга JSON использовать аннотацию gson @SerializedName.
        //@TODO protected заменить на private, дописать геттеры и сеттеры
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

    //@TODO Удалить пустой конструктор
    public TelegramGetUpdatesStruct() {

    }

    public List<TelegramResultStruct> getResult() {
        return result;
    }

    //@TODO Упростить логику, слишком сложно написано, проверять на isEmpty не нужно
    public boolean getValidateAnswer() {
        if (ok == true && !result.isEmpty())
            return true;
        return false;
    }

}

