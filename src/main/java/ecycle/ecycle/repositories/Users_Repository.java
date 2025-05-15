package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;


@Repository

public interface Users_Repository extends JpaRepository<User, Integer> {

    User findById (int id);
    User findByUsername (String username);
    User findByEmail (String email);
    User findByUsernameAndPassword(String username, String password);
    @NonNull List<User> findAll();
    void deleteById (int id);

}
