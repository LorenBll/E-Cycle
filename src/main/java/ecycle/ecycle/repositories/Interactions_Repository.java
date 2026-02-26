package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ecycle.ecycle.models.User;

/**
 * repository interface for managing interaction entities
 */
@Repository
public interface Interactions_Repository extends JpaRepository<Interaction, Integer> {

    /**
     * finds an interaction by its id
     * @param id the id to search for
     * @return the interaction with the specified id or null if not found
     */
    Interaction findById (int id);
    
    /**
     * finds interactions by user and type (offer or request)
     * @param user the user associated with the interactions
     * @param isOffer whether the interaction is an offer (true) or a request (false)
     * @return list of interactions matching the criteria
     */
    List<Interaction> findByUserAndIsOffer(User user, boolean isOffer);
    
    /**
     * deletes all interactions associated with a specific user
     * @param user the user whose interactions should be deleted
     */
    void deleteByUser(User user);
}
