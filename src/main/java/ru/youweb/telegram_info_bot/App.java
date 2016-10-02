package ru.youweb.telegram_info_bot;

//@TODO Удалить неиспользуемый импорт
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//@TODO Удалить ненужные комментарии(во всех классах)
/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TelegramAPI tApi = new TelegramAPI();
        WorkDB workDB = new WorkDB();
        //@TODO не нужно использовать метод con, в IDEA есть шаблоны для часто используемых операций
        //например для System.out.println пишешь sout и жмешь TAB.
        con("начало цикла");
        while(true) {
            for (TelegramGetUpdatesStruct.TelegramMessageStruct message : tApi.update()) {
                workDB.addUser(message.from.id, message.from.first_name + " " + message.from.last_name);
                con("Добавлено в бд юзер");
                tApi.sendAnswer(message.from.id, "мегОтвет");
                con("отправден ответ");
            }
            //@TODO Удалить, вместо этого используй Long pooling HTTP Request, определи для HTTP клиента и сервера
            //свойство request timeout(просто timeout для сервера) и клиентский таймаут должен быть обязательно больше серверного(timeoute в 10 секунд для клиента и 7 для сервера вполне нормально)
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void con(String str) {
        System.out.println(str);
    }

}
