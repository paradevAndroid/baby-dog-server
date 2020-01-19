package com.bramix.entities.additional;

import com.bramix.entities.Worker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = { "id", "workerFullBusy", "workerNotFullBusy" })
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer hourOfDay;
    private Integer minute;
    private Integer second;

    @ManyToOne
    @JoinColumn
    private Worker workerFullBusy;
    @ManyToOne
    @JoinColumn
    private Worker workerNotFullBusy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return  Objects.equals(year, schedule.year) &&
                Objects.equals(month, schedule.month) &&
                Objects.equals(dayOfMonth, schedule.dayOfMonth) &&
                Objects.equals(hourOfDay, schedule.hourOfDay) &&
                Objects.equals(minute, schedule.minute) &&
                Objects.equals(second, schedule.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, dayOfMonth, hourOfDay, minute, second);
    }
}