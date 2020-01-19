package com.bramix.entities;

import com.bramix.entities.additional.Schedule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class Worker extends Account {


    @Type(type = "text")
    private String document1;
    @Type(type = "text")
    private String document2;
    private Double averageRating;
    private String skills;
    private String contactPhone2;
    @NotNull
    private Integer sizeOfDog = 1;
    private Integer price_period = 0;
    @NotNull
    private Integer price = 0;
    private Integer countOfPets = 1;
    @NotNull
    private Boolean takePups = false;
    @NotNull
    private Boolean hasChildrens = false;
    @NotNull
    private Boolean isDogHandler = false;
    @NotNull
    private Boolean canMakeInjection = false;
    @NotNull
    private Boolean canGiveMedicine = false;
    @NotNull
    private Boolean haveAnimals = false;
    private Integer fatherWorkerId = null;
    private Integer repeatingOrders = 0;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private Set<Review> reviews;

     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
     @OneToMany (mappedBy = "workerFullBusy", cascade = CascadeType.ALL)
     private Set<Schedule> listFullyBusy ;
     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
     @OneToMany (mappedBy = "workerNotFullBusy", cascade = CascadeType.ALL)
     private Set<Schedule> listPartiallyOccupied;

     public void addReview (Review review){
        reviews.add(review);
    }

     public Worker() {}
}
