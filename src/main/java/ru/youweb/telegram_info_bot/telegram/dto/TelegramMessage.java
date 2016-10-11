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

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TelegramAuthorInfo getFrom() {
        return from;
    }

    public void setFrom(TelegramAuthorInfo from) {
        this.from = from;
    }
}
