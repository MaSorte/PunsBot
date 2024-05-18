package io.proj3ct.telegramjokebot.service;

import com.vdurmont.emoji.EmojiParser;
import io.proj3ct.telegramjokebot.config.BotConfig;
import io.proj3ct.telegramjokebot.model.PunRepository;
import io.proj3ct.telegramjokebot.model.PuriPuns;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@Service
@RestController
@Slf4j
@RequestMapping("/api")
public abstract class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    @Autowired
    private PunRepository punRepository;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "начало работы"));
        listOfCommands.add(new BotCommand("/pun", "выведет шутку"));
        listOfCommands.add(new BotCommand("/random_pun", "выведет рандомную шутку"));
        listOfCommands.add(new BotCommand("/all_pun", "выведет все шутки"));
        listOfCommands.add(new BotCommand("/add", "шутку писать за командой"));
        listOfCommands.add(new BotCommand("/pun_id", "id писать за командой"));
        listOfCommands.add(new BotCommand("/help", "ничего такого"));
        listOfCommands.add(new BotCommand("/del", "удалить шутку"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    static final String HELP_TEXT = """
            Эта Система не знает, как может помочь Пользователю.
            У этой Системы нет для тебя совета.\s
            (ᓀ ᓀ)
            Хочешь шутку, Пользователь? /pun""";

    public int getTotalPunsCount() {
        int totalPunsCount = 0;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS body FROM puri_puns;");
            if (resultSet.next()) {
                totalPunsCount = resultSet.getInt("body");
            }
        } catch (SQLException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return totalPunsCount;
    }
    private int currentPunIndex = 0;

    @GetMapping("/start")
    public String startCommand() {
        return "Привет, дорогой Пользователь.\nКоличество доступных шуток: " + getTotalPunsCount();
    }
    @GetMapping("/help")
    public String helpCommand() {
        return HELP_TEXT;
    }
    @GetMapping("/pun")
    public void punn(long chatId) {
        var pp = punRepository.findById((long) (currentPunIndex % getTotalPunsCount() + 1));
        if (pp.isPresent()) {
            sendMessage("Шутка №" + (currentPunIndex + 1) + ":\n" + pp.get().getBody()
                    + "\nДата создания: " + pp.get().getDateCreate(), chatId);
            currentPunIndex = (currentPunIndex + 1) % getTotalPunsCount();
        }
    }

    @GetMapping("/random_pun")
    public void rand_pun(long chatId) {
        var r = new Random();
        var randomId = r.nextInt(getTotalPunsCount()) + 1;
        var pp = punRepository.findById((long) randomId);
        pp.ifPresent(randomPun -> sendMessage("Шутка #" + (randomId + 1) + ":\n" + randomPun.getBody()
                + "\nДата создания: " + pp.get().getDateCreate(), chatId));
    }

    @PostMapping("/add")
    public void handleAddCommand(@RequestParam("text") String messageText,
                                 @RequestParam("chat_id") long chatId,
                                 Update update) {
        if (messageText.startsWith("/add ")) {
            var text = update.getMessage().getText().substring(update.getMessage().getText().indexOf(" ") + 1).trim();
            int Id = getTotalPunsCount() + 1;
            PuriPuns newpun = new PuriPuns();
            newpun.setId(Id);
            newpun.setBody(text);
            newpun.setDateCreate(new Date(System.currentTimeMillis()));
            punRepository.save(newpun);
            sendMessage("Шутка успешно добавлена", chatId);
        } else {
            sendMessage(EmojiParser.parseToUnicode("Вы не ввели шутку."), chatId);
        }
    }

    @GetMapping("/all_pun")
    public void sendAllPuns(long chatId) {
        for (int i = 1; i <= getTotalPunsCount(); i++) {
            var pun = punRepository.findById((long) i);
            int finalI = i;
            pun.ifPresent(puriPuns -> sendMessage("Шутка " + finalI + ":\n" + puriPuns.getBody()
                    + "\nДата создания: " + puriPuns.getDateCreate(), chatId));
        }
    }

    @DeleteMapping("/del")
    public void del(long chatId, Update update) {
        String text = update.getMessage().getText();
        int punId = Integer.parseInt(text.substring(text.indexOf(" ") + 1).trim());
        punRepository.deleteById(Long.valueOf(punId));
        sendMessage("Шутка с id " + punId + " удалена", chatId);
        List<PuriPuns> puns = punRepository.findAll();
        for (PuriPuns pun : puns) {
            if (pun.getId() > punId) {
                pun.setId(pun.getId() - 1);
                punRepository.save(pun);
            }
        }
    }

    @GetMapping("/pun_id")
    public void pun_id(long chatId, Update update) {
        String text = update.getMessage().getText();
        long punId = Integer.parseInt(text.substring(text.indexOf(" ") + 1).trim());
        var pp = punRepository.findById(punId);
        if (pp.isPresent()) {
            sendMessage("Шутка №" + punId + ":\n" + pp.get().getBody()
                    + "\nДата создания: " + pp.get().getDateCreate(), chatId);
        }
    }

    private void sendMessage(String textToSend, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        send(message);
    }
    private void send(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
