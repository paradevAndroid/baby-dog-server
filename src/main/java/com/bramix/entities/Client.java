package com.bramix.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter

@Entity
public class Client extends Account  {

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Pet> pets;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Review> reviews;

    { pets = new HashSet<Pet>(); }

    public Client() {}

    public void addPet (Pet pet) {
        pets.add(pet);
    }
    public void removePet (Pet pet){
        pets.remove(pet);
    }


}