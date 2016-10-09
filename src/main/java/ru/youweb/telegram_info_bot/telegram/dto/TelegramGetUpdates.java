package ru.youweb.telegram_info_bot.telegram.dto;

import java.util.List;

//@TODO Добавить отсутствующие сеттеры и геттеры
public class TelegramGetUpdates {

    private boolean ok;

    private List<TelegramResult> result;

    public List<TelegramResult> getResult() {
        return result;
    }
}

