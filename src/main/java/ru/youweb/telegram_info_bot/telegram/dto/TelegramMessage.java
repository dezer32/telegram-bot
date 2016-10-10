package ru.youweb.telegram_info_bot.telegram.dto;

import com.google.gson.annotations.SerializedName;

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
