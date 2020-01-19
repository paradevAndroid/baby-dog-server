package com.bramix.repos;

import com.bramix.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ClientRepository extends JpaRepository<Client, Integer> {
    @Query("SELECT c from Client c where c.tokenId = :token and c.type = :type")
    Client getClient(String token, String type);
    @Query("SELECT CASE WHEN count(c) > 0 THEN TRUE ELSE FALSE END from Client c where c.tokenId = :token and c.type = :type")
    boolean existsClient(String token, String type);
    boolean existsByContactPhone1(String Phone);
    boolean existsByTokenId(String tokenId);
    @Transactional
    @Modifying
    void removeById(Integer id);
    Client findById(int id);
    @Transactional
    @Modifying
    @Query ("update Client c set c.account_enabled = :enabled where c.id= :id")
    void updateById(boolean enabled, Integer id);

    Optional <Client> findByTokenId(String tokenId);
    Optional<Client> findByContactPhone1(String phone);
}