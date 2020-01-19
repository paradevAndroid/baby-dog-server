package com.bramix.repos;

import com.bramix.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetsRepository extends JpaRepository<Pet, Integer> {
    //@Query("SELECT p from Pet p where p.client_id = :id ")
    //ArrayList <Pet> findPetsByClientId (int id);
    //@Query("SELECT CASE WHEN count(p) > 0 THEN TRUE ELSE FALSE END from Pet p where p.client_id = :id")
    //boolean existsPetByClientId (int id);

}
