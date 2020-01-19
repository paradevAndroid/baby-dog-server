package com.bramix.repos;

import com.bramix.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AddressRepository extends JpaRepository <Address, Integer> {

    @Transactional
    @Modifying
    @Query("update Address w set w.apartment = :apartment, w.city = :city, w.country =:country" +
             ", w.district = :district, w.house = :house, w.privateHouse = :privateHouse, w.street = :street"
            + " where w.id = :id")
    void updateAll(Integer id, String apartment, String house, String city, String country,
                   String district, Boolean privateHouse, String street);

    // @Query ("Select a from Address a where a.worker_id = :id")
   // Address findByWorkerId (Integer id);
   // @Query("SELECT CASE WHEN count(a) > 0 THEN TRUE ELSE FALSE END from Address a where a.worker_id = :id")
    //boolean existsByWorkerId (Integer id);
}
