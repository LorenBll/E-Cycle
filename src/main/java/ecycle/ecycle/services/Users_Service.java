package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Users_Repository;
import ecycle.ecycle.models.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling user operations.
 */
@Service
@RequiredArgsConstructor
public class Users_Service {
    
    private final Users_Repository usersRepository;
    
    /**
     * finds a user by their username.
     * 
     * @param username the username to search for
     * @return the user if found, null otherwise
     */
    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    /**
     * finds a user by their email address.
     * 
     * @param email the email to search for
     * @return the user if found, null otherwise
     */
    public User findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    /**
     * saves a user to the database.
     * 
     * @param user the user to save
     * @return the saved user with updated information
     */
    public User save(User user) {
        return usersRepository.save(user);
    }

    /**
     * deletes a user from the database.
     * 
     * @param user the user to delete
     */
    @Transactional
    public void delete(User user) {
        usersRepository.deleteById(user.getId());
    }

}

