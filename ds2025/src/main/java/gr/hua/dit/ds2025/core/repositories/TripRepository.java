package gr.hua.dit.ds2025.core.repositories;

import gr.hua.dit.ds2025.core.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByDriverId(long driverId);

    List<Trip> findAllByPassengersId(long passengerId);

    List<Trip> findAllByDepartureTimeAfterAndAvailableSeatsGreaterThan(LocalDateTime time, int seats);

    List<Trip> findAllByDriverIdAndDepartureTimeAfter(Long driverId, LocalDateTime time);

    List<Trip> findAllByPassengersIdAndDepartureTimeAfter(Long passengerId, LocalDateTime time);

    @Query("""
           select t.id
           from Trip t
           where t.departureTime >= :from
             and t.departureTime < :to
           """)
    List<Long> findTripIdsDepartingBetween(@Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

}
