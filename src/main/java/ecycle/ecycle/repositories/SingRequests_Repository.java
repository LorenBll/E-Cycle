package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.SingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ecycle.ecycle.models.Characteristics;
import org.springframework.lang.NonNull;
import ecycle.ecycle.models.Interaction;

/**
 * repository interface for managing sing request entities
 */
@Repository
public interface SingRequests_Repository extends JpaRepository<SingRequest, Integer> {

    /**
     * finds a sing request by its id
     * @param id the id to search for
     * @return the sing request with the specified id or null if not found
     */
    SingRequest findById (int id);
    
    /**
     * finds all sing requests associated with a specific interaction request
     * @param request the interaction request to search for
     * @return list of sing requests matching the given request
     */
    List<SingRequest> findByRequest(Interaction request);
    
    /**
     * finds all active sing requests with specific characteristics
     * @param characteristics the characteristics to match
     * @return list of non-deleted sing requests with the specified characteristics
     */
    List<SingRequest> findByCharacteristicsAndTsDeletionIsNull(Characteristics characteristics);
    
    /**
     * retrieves all sing requests
     * @return list of all sing requests
     */
    @NonNull List<SingRequest> findAll();

    /**
     * deletes all sing requests associated with a specific interaction request
     * @param request the interaction request whose sing requests should be deleted
     */
    void deleteByRequest(Interaction request);
}
