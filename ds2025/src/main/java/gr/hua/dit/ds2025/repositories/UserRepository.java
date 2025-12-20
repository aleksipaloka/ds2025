package gr.hua.dit.ds2025.repositories;

import gr.hua.dit.ds2025.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
