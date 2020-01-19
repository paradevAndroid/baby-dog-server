package com.bramix.repos;

import com.bramix.entities.Worker;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DetachRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void detachUser (Worker worker){
        entityManager.detach(worker);
    }
}
