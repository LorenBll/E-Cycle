package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Users_Repository;
import ecycle.ecycle.models.User;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Users_Service {
    
    private final Users_Repository usersRepository;
    private final Interactions_Service interactionsService;
    
    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public User save(User user) {
        return usersRepository.save(user);
    }

    @Transactional
    public void delete(User user) {
        interactionsService.deleteByUser(user);
        usersRepository.deleteById(user.getId());
    }

}

