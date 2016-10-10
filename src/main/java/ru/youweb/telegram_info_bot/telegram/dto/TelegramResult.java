package ru.youweb.telegram_info_bot.telegram.dto;

import com.google.gson.annotations.SerializedName;

public class TelegramResult {
    @SerializedName("update_id")
    private int updateId;
    private TelegramMessage message;

    public int getUpdateId() {
        return updateId;
    }

    public TelegramMessage getMessage() {
        return message;
    }
}
