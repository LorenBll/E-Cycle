package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ecycle.ecycle.models.User;

@Repository
public interface Interactions_Repository extends JpaRepository<Interaction, Integer> {

    Interaction findById (int id);
    List<Interaction> findByUserAndIsOffer(User user, boolean isOffer);
    
    void deleteByUser(User user);

}
