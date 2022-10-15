package com.kanni.springCache.model;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Calendar;

@Data
@Entity
@Table(name = "HomeWages")
public class HomeWages {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wagesId")
    private int wagesId;
    @Column
    private String wagesType;
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //"2022-10-14T05:47:08.644"
    private LocalDateTime wagesDate;
    @Column
    private double amount;

}
