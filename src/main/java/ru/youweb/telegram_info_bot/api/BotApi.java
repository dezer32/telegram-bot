package ru.youweb.telegram_info_bot.api;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public class BotApi {

    private Map<String, BiConsumer<Request, Response>> routing = new HashMap<>();

    public BotApi on(String incoming, BiConsumer<Request, Response> function) {
        routing.put(incoming, function);
        return this;
    }

    public void run(final TelegramApi telegramApi) {
        boolean running = true;
        while (running) {
            try {
                telegramApi.update().parallelStream().forEach(message -> {
                    String text = message.getText();
                    if (text != null) {
                        List<String> commandWithArgs = Splitter.on(" ").splitToList(text);
                        if (commandWithArgs.size() > 0) {
                            BiConsumer<Request, Response> function = routing.get(commandWithArgs.get(0));
                            if (function != null) {
                                Request request = new Request();
                                request.setParams(commandWithArgs.subList(1, commandWithArgs.size()));
                                Response response = new Response();
                                function.accept(request, response);
                                if (!Strings.isNullOrEmpty(response.getContent())) {
                                    telegramApi.sendAnswer(message.getFrom().getId(), response.getContent());
                                }
                            }
                        }
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static BotApi create() {
        return new BotApi();
    }

    public static final class Request {

        private List<String> params;

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }
    }

    public static final class Response {

        private String content;

        public String getContent() {
            return content;
        }

        public Response setContent(String content) {
            this.content = content;
            return this;
        }

    }

}
