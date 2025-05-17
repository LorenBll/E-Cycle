package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Users_Repository;
import ecycle.ecycle.models.User;
import java.util.List;

@Service @RequiredArgsConstructor
public class Users_Service {
    
    private final Users_Repository users_repository;

    public User findById(int id) {
        return users_repository.findById(id);
    }

    public User findByUsername(String username) {
        return users_repository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return users_repository.findByEmail(email);
    }

    public User login(String username, String password) {
        return users_repository.findByUsernameAndPassword(username, password);
    }

    public User findByUsernameAndPassword(String username, String password) {
        return users_repository.findByUsernameAndPassword(username, password);
    }

    public List<User> findAll() {
        return users_repository.findAll();
    }

    // register a new user
    public User register(User user) {
        return users_repository.save(user);
    }

    // update user
    public User update(User user) {
        return users_repository.save(user);
    }

    // delete user
    public void delete(int id) {
        users_repository.deleteById(id);
    }
}
