package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Users_Repository;
import ecycle.ecycle.models.User;
import ecycle.ecycle.services.Interactions_Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class Users_Service {
    
    private final Users_Repository usersRepository;
    private final Interactions_Service interactionsService;

    public User findById(int id) {
        return usersRepository.findById(id);
    }

    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public User login(String username, String password) {
        return usersRepository.findByUsernameAndPassword(username, password);
    }

    public User findByUsernameAndPassword(String username, String password) {
        return usersRepository.findByUsernameAndPassword(username, password);
    }

    public List<User> findAll() {
        return usersRepository.findAll();
    }

    // register a new user
    public User save(User user) {
        return usersRepository.save(user);
    }

    // update user
    public User update(User user) {
        return usersRepository.save(user);
    }

    // delete user
    public void delete(User user) {
        interactionsService.deleteByUser(user);
        usersRepository.deleteById(user.getId());
    }

}
