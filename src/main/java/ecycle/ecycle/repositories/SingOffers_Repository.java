package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.SingOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import ecycle.ecycle.models.Interaction;

/**
 * repository interface for managing sing offer entities
 */
@Repository
public interface SingOffers_Repository extends JpaRepository<SingOffer, Integer> {

    /**
     * finds a sing offer by its id
     * @param id the id to search for
     * @return the sing offer with the specified id or null if not found
     */
    SingOffer findById(int id);
    
    /**
     * finds all sing offers associated with a specific interaction offer
     * @param offer the interaction offer to search for
     * @return list of sing offers matching the given offer
     */
    List<SingOffer> findByOffer(Interaction offer);
    
    /**
     * retrieves all sing offers
     * @return list of all sing offers
     */
    @NonNull List<SingOffer> findAll();
    
    /**
     * finds all active sing offers that have not expired
     * @param date the date to compare against for expiration
     * @return list of non-deleted sing offers that expire after the specified date
     */
    List<SingOffer> findByTsDeletionIsNullAndExpirationAfter(java.sql.Date date);

    /**
     * deletes all sing offers associated with a specific interaction offer
     * @param offer the interaction offer whose sing offers should be deleted
     */
    void deleteByOffer(Interaction offer);
}
