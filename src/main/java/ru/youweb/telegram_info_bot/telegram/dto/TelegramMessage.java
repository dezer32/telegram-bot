package ru.youweb.telegram_info_bot.telegram.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Youweb on 03.10.2016.
 */
public class TelegramMessage {
    @SerializedName("message_id")
    private int messageId;
    private int date;
    private String text;
    private TelegramAuthorInfo from;

    public int getMessageId() {
        return messageId;
    }

    public String getText() {
        return text;
    }

    public TelegramAuthorInfo getFrom() {
        return from;
    }
}
