package ru.youweb.telegram_info_bot.telegram.dto;

import com.google.gson.annotations.SerializedName;

public class TelegramAuthorInfo {
    private int id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
