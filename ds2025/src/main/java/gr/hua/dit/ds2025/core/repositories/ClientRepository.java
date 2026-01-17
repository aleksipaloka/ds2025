package gr.hua.dit.ds2025.core.repositories;

import gr.hua.dit.ds2025.core.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByName(final String name);
}