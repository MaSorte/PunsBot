package io.proj3ct.telegramjokebot.service;

import io.proj3ct.telegramjokebot.model.PunRepository;
import io.proj3ct.telegramjokebot.model.PuriPuns;

import java.util.List;
import java.util.Optional;

public class PunServiceImprt implements PunService{

    private PunRepository punRepository;

    @Override
    public void registerPun(PuriPuns pun) {
        punRepository.save(pun);
    }

    @Override
    public List<PuriPuns> getAllPun() {
        return punRepository.findAll();
    }

    @Override
    public Optional<PuriPuns> getPunsById(Long id) {
        return punRepository.findById(id);
    }

}