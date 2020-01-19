package com.bramix.repos;

import com.bramix.entities.Worker;
import com.bramix.entities.additional.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long > {
    @Transactional
    @Modifying
    @Query ("delete from Schedule s where s.workerFullBusy = :worker or s.workerNotFullBusy =:worker")
    void removeByWorker (Worker worker);
}
