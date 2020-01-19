package com.bramix.repos;

import com.bramix.entities.Review;
import com.bramix.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository <Review, Long> {

    @Modifying
    @Transactional
    @Query ("update Review r set r.comment = :comment, r.mark = :mark where r.id = :id and r.worker = :worker")
    void updateById (String comment, Integer mark, Long id, Worker worker);

    @Query ("select avg(r.mark) from Review r where r.worker = :worker group by r.worker")
    Optional<Double> selectAvg (Worker worker);

    @Modifying
    @Transactional
    @Query (value = "delete from Review where client_id = ?1 and worker_id = ?2", nativeQuery = true)
    void deleteIfExist (Integer client_id, Integer worker_id);
}
