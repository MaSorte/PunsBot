package io.proj3ct.telegramjokebot.controller;

import io.proj3ct.telegramjokebot.model.PuriPuns;
import io.proj3ct.telegramjokebot.service.PunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api")
public class RestControll {
    private final PunService punService;

    public RestControll(PunService punService) {
        this.punService = punService;
    }

    @GetMapping("/start")
    public String startCommand() {
        int tt = (int) punService.getPunCount();
        return "Привет, дорогой Пользователь.\nКоличество доступных шуток: " + tt;
    }

    @GetMapping("/help")
    public String helpCommand() {
        return """
            Эта Система не знает, как может помочь Пользователю.
            У этой Системы нет для тебя совета.\s
            (ᓀ ᓀ)
            Хочешь шутку, Пользователь? /pun""";
    }

    private int currentPunIndex = 0;
    @GetMapping("/pun")
    public ResponseEntity<String> getPun() {
        int q = (int) punService.getPunCount();
        var pp = punService.getPunById((long) (currentPunIndex % q + 1));
        if (pp.isPresent()) {
            String message = "Шутка №" + (currentPunIndex + 1) + ":\n" + pp.get().getBody()
                    + "\nДата создания: " + pp.get().getDateCreate();
            currentPunIndex = (currentPunIndex + 1) % q;
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/allpuns")
    public List<PuriPuns> getAllPuns() {
        return punService.getAllPuns();
    }

    @GetMapping("/pun/{id}")
    public Optional<PuriPuns> getPunById(@PathVariable Long id) {
        return punService.getPunById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePunById(@RequestBody String id) {
        if (punService.deletePun(Long.valueOf(id))) {
            return ResponseEntity.ok("Шутка с ID " + id + " удалена.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}