package ru.youweb.telegram_info_bot.telegram.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Youweb on 03.10.2016.
 */
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
