package com.bramix.repos;

import com.bramix.controllers.StatusController;
import com.bramix.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository <Worker, Integer >  {

    boolean existsByContactPhone1(String Phone);

    boolean existsByFatherWorkerId(Integer id);


    @Query(value = "select * from Worker w WHERE w.contact_phone1 = ?1 LIMIT 1", nativeQuery = true)
    Optional<Worker> findByContactPhone1(String phone);

//    @Query("select w from Worker w WHERE w.contactPhone1 = ?1")
//    Optional<Worker> findByContactPhone1(String phone);
    
    List<Worker> findAllByFatherWorkerIdNotNull();
    Worker findByFatherWorkerId(Integer WorkerId);


    @Query ("select w from Worker w inner join w.address a where (a.district LIKE :district% or :district is null) " +
            " and (a.city LIKE :city% or :city is null) and (a.street LIKE :street% or :street is null) "   +
            " and (w.hasChildrens = :hasChildrens or :hasChildrens is null) " +
            " and (a.privateHouse =:privateHouse or :privateHouse is null) and (w.isDogHandler = :isDogHandler or :isDogHandler is null )" +
            " and (w.canGiveMedicine = :canGiveMedicine or :canGiveMedicine is null ) and (:price is null or w.price <= :price )" +
            " and (:sizeOfDog is null or w.sizeOfDog <= :sizeOfDog ) and (:countOfPets is null or w.countOfPets >= :countOfPets ) " )
    List <Worker> findTest (@RequestParam String district, String city, String street, Boolean hasChildrens, Boolean privateHouse,
                            Boolean isDogHandler, Boolean canGiveMedicine, Integer price, Integer sizeOfDog, Integer countOfPets );

    @Transactional
    @Modifying
    @Query ("update Worker w set w.averageRating = :averageRating where w.id = :id")
    void updateReview (Double averageRating, Integer id);
    @Transactional
    @Modifying
    @Query ("update Worker w set w.document1 = :document1 where  w.contactPhone1 = :phone")
    void updateByPhone1(String document1, String phone);
    @Transactional
    @Modifying
    @Query ("update Worker w set w.document2 = :document2 where  w.contactPhone1 = :phone")
    void updateByPhone2(String document2, String phone);
    @Transactional
    @Modifying
    @Query ("update Worker w set w.account_enabled = :enabled where w.id= :id")
    void updateById(boolean enabled, Integer id);
    @Transactional
    @Modifying
    void removeById(Integer id);
    @Transactional
    @Modifying
    @Query ("delete from Worker w where w.fatherWorkerId = :id")
    void removeByFatherWorkerId (Integer id);

    @Transactional
    @Modifying
    @Query("update Worker w set w.firstName = :firstName, w.lastName = :lastName" +
            ", w.contactPhone1 = :contactPhone1, w.contactPhone2 = :contactPhone2 " +
            ", w.skills = :skills, w.document1=:document1 , w.document2=:document2 where w.id = :id")
    void updateAll(Integer id, String firstName, String lastName, String contactPhone1, String contactPhone2,
                   String document1, String document2, String skills);

    @Transactional
    @Modifying
    @Query("update Worker w set w.sizeOfDog = :sizeOfDog, w.price = :price" +
            ", w.price_period = :price_period, w.countOfPets = :countOfPets" +
            ", w.takePups = :takePups, w.hasChildrens=:hasChildrens , w.isDogHandler=:isDogHandler" +
            ", w.canMakeInjection = :canMakeInjection, w.canGiveMedicine = :canGiveMedicine, w.haveAnimals = :haveAnimals " +
            " where w.id = :id")
    void updateWithoutChecking(Integer id, Integer sizeOfDog, Integer price, Integer price_period,
                               Integer countOfPets, Boolean takePups, Boolean hasChildrens, Boolean isDogHandler,
                               Boolean canGiveMedicine, Boolean canMakeInjection, Boolean haveAnimals);

}
