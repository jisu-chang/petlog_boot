package com.example.PetLog.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntity, Long> {

    @Query("SELECT c FROM CalendarEntity c " +
            "WHERE FUNCTION('YEAR', c.calDate) = :year " +
            "AND FUNCTION('MONTH', c.calDate) = :month " +
            "AND c.userId = :userId " +
            "AND c.petId = :petId")
    List<CalendarEntity> findCalendarListByMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("petId") Long petId
    );
}



