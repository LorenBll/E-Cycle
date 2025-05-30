package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface Users_Repository extends JpaRepository<User, Integer> {

    User findByUsername (String username);
    User findByEmail (String email);
    
    void deleteById (int id);

}

