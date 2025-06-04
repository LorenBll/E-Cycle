package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * repository interface for user entity operations
 */
@Repository
public interface Users_Repository extends JpaRepository<User, Integer> {

    /**
     * finds a user by their username
     * @param username the username to search for
     * @return the user with the specified username or null if not found
     */
    User findByUsername (String username);
    
    /**
     * finds a user by their email address
     * @param email the email to search for
     * @return the user with the specified email or null if not found
     */
    User findByEmail (String email);
    
    /**
     * deletes a user by their id
     * @param id the id of the user to delete
     */
    void deleteById (int id);
}

