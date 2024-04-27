package io.proj3ct.telegramjokebot.service;

import io.proj3ct.telegramjokebot.model.PuriPuns;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Controller
public interface PunService {

    void registerPun(PuriPuns pun);

    List<PuriPuns> getAllPun();

    Optional<PuriPuns> getPunsById(Long id);

}
