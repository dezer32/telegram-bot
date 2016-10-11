package ru.youweb.telegram_info_bot.telegram.dto;

import java.util.List;

public class TelegramGetUpdates {

    private boolean ok;

    private List<TelegramResult> result;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<TelegramResult> getResult() {
        return result;
    }

    public void setResult(List<TelegramResult> result) {
        this.result = result;
    }
}

