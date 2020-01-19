package com.bramix.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nickname;
    private int ageVal;
    private String breed;
    private boolean puppy;
    private int sizeOfDog;
    private boolean gender;
    private String addInfo;
    private boolean isVaccinated;
    private boolean isSterilized;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Client client;

    public Pet(String nickname, int ageVal, String breed, boolean puppy, int sizeOfDog, boolean gender, String addInfo, boolean isVaccinated, boolean isSterilized) {
        this.nickname = nickname;
        this.ageVal = ageVal;
        this.breed = breed;
        this.puppy = puppy;
        this.sizeOfDog = sizeOfDog;
        this.gender = gender;
        this.addInfo = addInfo;
        this.isVaccinated = isVaccinated;
        this.isSterilized = isSterilized;
    }
}
