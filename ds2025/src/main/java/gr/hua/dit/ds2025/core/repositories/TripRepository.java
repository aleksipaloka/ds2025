package gr.hua.dit.ds2025.core.repositories;

import gr.hua.dit.ds2025.core.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
}
