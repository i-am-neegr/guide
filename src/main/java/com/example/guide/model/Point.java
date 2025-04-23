package com.example.guide.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Point {
    @Id
    @GeneratedValue
    private long ID;

    private Double lat;
    private Double lon;

    @JsonIgnore
    @OneToMany
    private Set<Path> neighbors;
}
