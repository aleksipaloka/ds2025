package gr.hua.dit.ds2025.core.repositories;

import gr.hua.dit.ds2025.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(final String name);

}
