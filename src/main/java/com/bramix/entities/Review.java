package com.bramix.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(value = { "worker", "client" })
public class Review {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer mark;
    private String comment;
    private String author;
   // private Integer client_id;

    @ManyToOne
    @JoinColumn
    private Worker worker;
    @ManyToOne
    @JoinColumn
    private Client client;
}
