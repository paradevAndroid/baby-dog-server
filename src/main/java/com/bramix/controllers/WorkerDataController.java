package com.bramix.controllers;

import com.bramix.Answer;
import com.bramix.Properties;
import com.bramix.entities.*;
import com.bramix.entities.additional.Schedule;
import com.bramix.repos.*;
import com.bramix.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping (path = "/worker")
@Slf4j
public class WorkerDataController {

    private final static String massiveOfChanges = "massiveOfChanges";

    private final WorkerRepository workerRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public WorkerDataController(WorkerRepository workerRepository, ReviewRepository reviewRepository, UserService userService, ScheduleRepository scheduleRepository, AddressRepository addressRepository) {
        this.workerRepository = workerRepository;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.addressRepository = addressRepository;
    }
    @PostMapping
    public void setAuthorize (HttpSession httpSession){
        httpSession.setAttribute(Properties.type.getProperty(), Properties.phone.getProperty());
        httpSession.setAttribute(Properties.id.getProperty(), "380500395871");
    }

    @PostMapping (path = "/addReview")
    public ResponseEntity <Answer> addReview(HttpSession httpSession, @RequestParam Integer id, @RequestBody Review review){
        if (httpSession.getAttribute(Properties.type.getProperty())  == null)
            return new ResponseEntity <Answer>(new Answer(false, "Not permitted"), HttpStatus.METHOD_NOT_ALLOWED);
        String clientID = (String) (httpSession.getAttribute(Properties.id.getProperty()));
        Client client = userService.getClientType(clientID);
        Optional <Worker> optional = workerRepository.findById(id);
        if (optional.isPresent()){
            Worker worker = optional.get();
            reviewRepository.deleteIfExist(client.getId(), worker.getId());
            worker.addReview(review);
            review.setWorker(worker);
            review.setClient(client);
            reviewRepository.save(review);
            double avg = reviewRepository.selectAvg(optional.get()).orElse((double)0);
            workerRepository.updateReview(avg,optional.get().getId());
            return new ResponseEntity <Answer>(new Answer(true, avg), HttpStatus.OK);
        }
        else
            return new ResponseEntity <Answer>(new Answer(false), HttpStatus.NOT_FOUND);
    }
    @GetMapping (value = "/getReviews")
    public Answer getReviews (@RequestParam Integer id){
        Optional <Worker> optional = workerRepository.findById(id);
        if (optional.isPresent()){
            Answer answer = new Answer(true, optional.get().getReviews());
            return answer;
        }
        else
            return new Answer(false, 404);
    }
    /*@PostMapping (value = "/addSchedule")
    public Answer addSchedule (HttpSession httpSession, @RequestBody DateType dateType){
       if (httpSession.getAttribute(Properties.type.getProperty())  == null)
           return new Answer(false, "Not permitted");
        log.info("datatype = {}", dateType);

        Worker worker = workerRepository.findByContactPhone1((String) httpSession.getAttribute(Properties.id.getProperty())).get();
       Set <Schedule> fullyBusy = Optional.ofNullable(dateType.getListFullyBusy()).orElse(new HashSet<>()).stream()
                .map(calendar -> {
                    Schedule schedule = new Schedule(calendar, worker, null);
                    schedule.set(worker);
                    return schedule;
                }).collect(Collectors.toSet());

        Set <Schedule> particiallyOccupied = Optional.ofNullable(dateType.getListPartiallyOccupied()).orElse(new HashSet<>()).stream()
                .map(calendar -> {
                    Schedule schedule = new Schedule( calendar, null, worker);
                    schedule.setWorkerFullBusy(worker);
                    return schedule;
                }).collect(Collectors.toSet());

        scheduleRepository.removeByWorker(worker);
        scheduleRepository.saveAll(fullyBusy);
        scheduleRepository.saveAll(particiallyOccupied);

        return new Answer(true, worker.provideLists());
    }*/
    @PostMapping ("/changes")
    public Answer dataForCheck (@RequestBody RegistrationEntity registrationEntity ) {
        Worker worker = registrationEntity.getWorker();
        Address address = registrationEntity.getAddress();
        worker.setAddress(address);
        if (!workerRepository.existsById(worker.getId()))
            return new Answer(false, "User with such id is not exist");
        if (workerRepository.existsByFatherWorkerId(worker.getId()))
            workerRepository.removeByFatherWorkerId(worker.getId());
        log.info("Worker for changes = {}", worker);
        worker.setFatherWorkerId(worker.getId());
        worker.setId(null);
        workerRepository.save(worker);
        return new Answer (true);
    }

    @GetMapping ("/changes")
    public Answer getDataForCheck (HttpSession httpSession) throws IOException {
        List<Worker> listOfWorkers = workerRepository.findAllByFatherWorkerIdNotNull();
        List <Change> listOfChanges = new ArrayList<>();
        listOfWorkers.forEach(worker ->  {
                    Worker workerBefore = workerRepository.findById(worker.getFatherWorkerId()).orElse(null);
                    Address addressBefore = workerBefore.getAddress();
                    RegistrationEntity beforeEntity = new RegistrationEntity(addressBefore, null, workerBefore);
                    RegistrationEntity afterEntity = new RegistrationEntity(worker.getAddress(), null, worker);
                    listOfChanges.add(new Change(beforeEntity, afterEntity));
                });
        return new Answer (true, listOfChanges);
    }
    @PostMapping ("/approveChanges")
    public Answer approveChanges (@RequestBody RegistrationEntity registrationEntity, @RequestParam Boolean isApproved) {
        Worker worker = registrationEntity.getWorker();
        Address address = registrationEntity.getAddress();
        workerRepository.removeByFatherWorkerId(worker.getFatherWorkerId());
        if (isApproved){
            workerRepository.updateAll(worker.getFatherWorkerId(), worker.getFirstName(),
                    worker.getLastName(), worker.getContactPhone1(), worker.getContactPhone2(), worker.getDocument1(), worker.getDocument2(), worker.getSkills());
            addressRepository.updateAll(address.getId(), address.getApartment(), address.getHouse(), address.getCity(),
                    address.getCountry(), address.getDistrict(), address.getPrivateHouse(), address.getStreet());
        }
        return new Answer (true);
    }
    @DeleteMapping (value = "/deleteReview")
    public Answer deleteReview (HttpSession httpSession, @RequestParam Long id) {
        if (httpSession.getAttribute(Properties.type.getProperty())  == null)
            return new Answer(false, "Not permitted");
        Worker worker = getClient(httpSession, id);
        if (worker == null)
            return new Answer (false, "Not permitted for this account");
        reviewRepository.deleteById(id);
        Double avg = reviewRepository.selectAvg (worker).orElse((double) 0);
        workerRepository.updateReview(avg,worker.getId());
        return new Answer (true, avg);
    }

    @PatchMapping (value = "/editReview")
    public Answer editReview (HttpSession httpSession, @RequestParam Long id, @RequestBody Review review){
        if (httpSession.getAttribute(Properties.type.getProperty())  == null)
            return new Answer(false, "Not permitted");
        Client client = userService.getClientType((String)httpSession.getAttribute(Properties.id.getProperty()));
        Review OldReview = reviewRepository.getOne(id);
        Worker worker = OldReview.getWorker();
        if (client == null || !client.equals(OldReview.getClient()) )//||// !OldReview.getClient_id().equals(client.getId()))
            return new Answer (false, "Not permitted for this account");
        reviewRepository.updateById(review.getComment(), review.getMark(), id, worker );
        double avg = reviewRepository.selectAvg(worker).orElse((double) 0);
        workerRepository.updateReview(avg,worker.getId());
        return new Answer(true, avg);
    }

    private Worker getClient (HttpSession httpSession, Long id){
        Client client = userService.getClientType((String) httpSession.getAttribute(Properties.id.getProperty()));
        Review review = reviewRepository.getOne(id);
        if (review.getClient().equals(client))
            return review.getWorker();
        return null;
    }
}
@Data
class DateType  {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Set <Schedule> listFullyBusy;
        private Set <Schedule> listPartiallyOccupied;
}

@Data
class Change {
    private RegistrationEntity before;
    private RegistrationEntity after;

    public Change(RegistrationEntity before, RegistrationEntity after) {
        this.before = before;
        this.after = after;
    }
}
