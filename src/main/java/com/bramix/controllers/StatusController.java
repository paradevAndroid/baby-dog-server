package com.bramix.controllers;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.Properties;
import com.bramix.entities.RegistrationEntity;
import com.bramix.entities.Worker;
import com.bramix.entities.additional.Filter;
import com.bramix.entities.additional.Schedule;
import com.bramix.repos.ScheduleRepository;
import com.bramix.repos.WorkerRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping ("/worker")
public class StatusController {
    private final WorkerDataController workerDataController;
    private final WorkerRepository workerRepository;
    private final ScheduleRepository scheduleRepository;
    public StatusController(WorkerDataController workerDataController, WorkerRepository workerRepository, ScheduleRepository scheduleRepository) {
        this.workerDataController = workerDataController;
        this.workerRepository = workerRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping("/changeStatus/{id}")
    @CrossOrigin
    public Answer changeStatus (HttpSession httpSession, @PathVariable Integer id) {
            if (workerRepository.existsById(id)) {
                Worker worker = workerRepository.getOne(id);
                workerRepository.updateById(!worker.isAccount_enabled(), worker.getId());
            }
            else return new Answer(false, ClientError.UserNotFound);
            return new Answer(true);
    }

    @GetMapping ("/getStatus")
    @CrossOrigin
    public Answer getStatus (HttpSession httpSession) {
        String id = (String) httpSession.getAttribute(Properties.id.getProperty());
        if (id == null) {
            log.info("Not permitted");
            return new Answer(false, "Not permitted");
        }
        if (workerRepository.existsByContactPhone1(id)) {
            Worker worker = workerRepository.findByContactPhone1(id).get();
            return new Answer(true , worker.isAccount_enabled() );
        }
        else return new Answer(false, ClientError.UserNotFound);
    }

    @PostMapping ("/update")
    public Answer updateWithoutCheck (HttpSession httpSession, @RequestBody Worker worker) {
        log.info(httpSession.getAttribute(Properties.id.getProperty()) + "");
        String id = (String) httpSession.getAttribute(Properties.id.getProperty());
        if (id == null) {
            return new Answer(false, "Not permitted");
        }
        Worker realWorker = workerRepository.findByContactPhone1(id).get();
        log.info("Worker for update = {} ", worker);
        workerRepository.updateWithoutChecking(realWorker.getId(), worker.getSizeOfDog(), worker.getPrice(), worker.getPrice_period(),
                worker.getCountOfPets(), worker.getTakePups(), worker.getHasChildrens(), worker.getIsDogHandler(), worker.getCanGiveMedicine(),
                worker.getCanMakeInjection(), worker.getHaveAnimals());
        scheduleRepository.removeByWorker(realWorker);
        Set<Schedule> fullyBusy = worker.getListFullyBusy();
        Set<Schedule> partBusy = worker.getListPartiallyOccupied();
        fullyBusy.forEach(schedule -> schedule.setWorkerFullBusy(realWorker));
        partBusy.forEach(schedule -> schedule.setWorkerNotFullBusy(realWorker));
        scheduleRepository.saveAll(fullyBusy);
        scheduleRepository.saveAll(partBusy);
        return new Answer(true);
    }

    @GetMapping ("/getWorkerById")
    public Answer getWorkerByid (@RequestParam(required = true) int id) {
        Optional <Worker> worker = workerRepository.findById(id);
        if (worker.isPresent()){
            RegistrationEntity registrationEntity = new RegistrationEntity(worker.get().getAddress(), null, worker.get());
            return new Answer(true, registrationEntity);
        }
        return new Answer (false, "User with such id does not exist");
    }

    @PostMapping ("/getWorkers")
    public Answer getWorkersByFilter (@RequestBody Filter filter,
                                      @RequestParam(required = true) int begin,
                                      @RequestParam (required = true) int size) {

        if (filter.price == null || filter.price == 0)
            filter.price = null;

        if(filter == null)
            return new Answer (false, "filter is null");

        if (!filter.isDogHandler) filter.isDogHandler = null;
        if (!filter.canGiveMedicine) filter.canGiveMedicine = null;
        if (!filter.canMakeInjection) filter.canMakeInjection = null;
        if (filter.hasChildren) filter.hasChildren = null;

        List <Worker> workers =  workerRepository.findTest(filter.district, filter.city, filter.street, filter.hasChildren,
                filter.privateHouse, filter.isDogHandler, filter.canGiveMedicine, filter.price, filter.sizeOfDog, filter.countOfPets);
        List<FilterDate> result = new ArrayList<>();
        boolean breakLoop = false;
        if (filter.listFullyBusy != null && !filter.listFullyBusy.isEmpty()) {
            for (Worker worker : workers) {
                Set<Schedule> listFullyBusy = worker.getListFullyBusy();
                for (Schedule schedule : listFullyBusy) {
                    if (breakLoop) {
                        breakLoop = false;
                        break;
                    }
                    for (Schedule scheduleF : filter.listFullyBusy) {
                        if (isSchedulesEqual(schedule, scheduleF)) {
                            result.add(formFilterItem(worker));
                            breakLoop = true;
                            break;
                        }
                    }
                }
            }
        }else {
            for (Worker worker: workers) {
                result.add(formFilterItem(worker));
            }
        }
        log.info("Size = " + result.size());
//        List <FilterDate> result = workers.stream()
//                    .filter(worker -> !Optional.ofNullable(filter.listFullyBusy).isPresent() ||
//                            (filter.listFullyBusy.stream().anyMatch(calendar -> !worker.getListFullyBusy().contains(calendar)
//                                     && !worker.getListPartiallyOccupied().contains(calendar))))
//                    .map(worker -> { return new FilterDate(worker.getFirstName(), worker.getLastName(), worker.getPhoto_mini(),
//                            worker.getPrice_period(), worker.getPrice(), worker.getAddress().getCity(), worker.getAddress().getStreet(),
//                            worker.getAddress().getDistrict(), worker.getAverageRating(), worker.getRepeatingOrders(), worker.getId());})
//                    .collect(Collectors.toList());
        if (result.size() < begin + 1)
                return new Answer (false, "Begin value is more then count of Workers");
        if (result.size() < begin + size  )
                 return new Answer (true, result.subList(begin, result.size() ));

        return new Answer(true, result);
    }

    private FilterDate formFilterItem(Worker worker){
        return new FilterDate(worker.getFirstName(), worker.getLastName(),
                worker.getPhoto_mini(), worker.getPrice_period(), worker.getPrice(), worker.getAddress().getCity(),
                worker.getAddress().getCity(), worker.getAddress().getDistrict(), worker.getAverageRating(),
                worker.getRepeatingOrders(),worker.getId());
    }

    private boolean isSchedulesEqual(Schedule schedule1, Schedule schedule2){
        if (schedule1.getYear().equals(schedule2.getYear()) &&
            schedule1.getMonth().equals(schedule2.getMonth()) &&
            schedule1.getDayOfMonth().equals(schedule2.getDayOfMonth())) return true;
        else return false;
    }

    @Data
    class FilterDate {
        private String firstName;
        private String lastName;
        private String photo_mini; //base64
        private int price_period = 0;
        private int price = 0;
        private String city;
        private String street;
        private String district;
        private Double averageRating; // средний рейтинг из отзывов
        private int repeatingOrders;
        private int id;

        public FilterDate(String firstName, String lastName, String photo_mini, int price_period,
                          int price, String city, String street, String district,
                          Double averageRating, int repeatingOrders, int id) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.photo_mini = photo_mini;
            this.price_period = price_period;
            this.price = price;
            this.city = city;
            this.street = street;
            this.district = district;
            this.averageRating = averageRating;
            this.repeatingOrders = repeatingOrders;
            this.id = id;
        }
    }
}
