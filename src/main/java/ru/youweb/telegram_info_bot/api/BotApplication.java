package ru.youweb.telegram_info_bot.api;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.youweb.telegram_info_bot.telegram.TelegramApi;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

abstract public class BotApplication {

    private static final Logger logger = LoggerFactory.getLogger(BotApplication.class);

    private final TelegramApi telegramApi;

    private final PebbleEngine pebbleEngine;

    private Map<String, BiConsumer<Request, Response>> routing = new HashMap<>();

    public BotApplication(final TelegramApi telegramApi, PebbleEngine pebbleEngine) {
        this.telegramApi = telegramApi;
        this.pebbleEngine = pebbleEngine;
        configure();
    }

    abstract protected void configure();

    protected BotApplication on(String incoming, BiConsumer<Request, Response> function) {
        routing.put(incoming, function);
        return this;
    }

    public void run() {
        boolean running = true;
        while (running) {
            try {
                telegramApi.update().parallelStream().forEach(message -> {
                    try {
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
                                        telegramApi.sendAnswer(message.getChat().getId(), response.getContent());
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("Ошибка в процессе обработки сообщения " + message, ex);
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public final class Request {

        private List<String> params;

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }

        public String getFirstParam() {
            return getParams() == null || getParams().size() == 0 ? "" : getParams().get(0);
        }
    }

    public final class Response {

        private String content;

        public String getContent() {
            return content;
        }

        public Response setContent(String content) {
            this.content = content;
            return this;
        }

        public Response setView(String view) {
            return setView(view, new HashMap<>());
        }

        public Response setView(String view, Map<String, Object> context) {
            try {
                PebbleTemplate template = pebbleEngine.getTemplate(view);
                Writer writer = new StringWriter();

                template.evaluate(writer, context);
                return setContent(writer.toString());
            } catch (PebbleException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

}
