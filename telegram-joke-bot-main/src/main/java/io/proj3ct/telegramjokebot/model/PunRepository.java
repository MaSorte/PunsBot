package io.proj3ct.telegramjokebot.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PunRepository extends JpaRepository<PuriPuns, Long> {

    List<PuriPuns> findAllByIdGreaterThan(Long punId);
}

