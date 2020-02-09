package com.bramix.entities.additional;

import java.util.Calendar;
import java.util.Set;

public class Filter {
    public String city;
    public String district;
    public String street;
    public Boolean privateHouse;
    public Boolean hasChildren;
    public Boolean isDogHandler ;
    public Boolean canMakeInjection ;
    public Boolean canGiveMedicine ;
    public Set<Schedule> listFullyBusy;
    public Integer sizeOfDog;
    public Integer countOfPets;
    public Integer price ;
}
