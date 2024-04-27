package io.proj3ct.telegramjokebot.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;

@Entity
@Data
@Table(name = "puri_puns")
public class PuriPuns {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="body", length = 2550000)
    private String body;

    @Column(name="date_create")
    private Date dateCreate;

}