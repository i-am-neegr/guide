package com.example.guide.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "path")
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_id", nullable = false)
    private Long startId;

    @Column(name = "end_id", nullable = false)
    private Long endId;

    @Column(name = "length", nullable = false)
    private Double length;

    @Column(name = "time", nullable = false)
    private Integer time;
}