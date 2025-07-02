package com.example.PetLog.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntity, Long> {

    @Query("SELECT c FROM CalendarEntity c " +
            "WHERE TO_CHAR(c.calDate, 'YYYY') = :year " +
            "AND TO_CHAR(c.calDate, 'MM') = LPAD(:month, 2, '0') " +
            "AND c.userId = :userId " +
            "AND c.petId = :petId")
    List<CalendarEntity> findCalendarListByMonth(
            @Param("userId") Long userId,
            @Param("year") String year,
            @Param("month") String month,
            @Param("petId") Long petId
    );

    @Query("SELECT new com.example.PetLog.Calendar.CalendarDTO(c.calId, c.calTitle, c.calContent, c.calDate, c.userId, c.petId, null) " +
            "FROM CalendarEntity c WHERE c.calId = :calId")
    CalendarDTO selectCalendarById(@Param("calId") Long calId);

    int countByUserId(Long userId);

    List<CalendarEntity> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
