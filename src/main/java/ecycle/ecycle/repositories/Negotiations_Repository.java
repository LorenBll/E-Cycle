package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Negotiation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import org.springframework.lang.NonNull;

/**
 * repository interface for managing negotiation entities
 */
@Repository
public interface Negotiations_Repository extends JpaRepository<Negotiation, Integer> {

    /**
     * finds a negotiation by its id
     * @param id the id to search for
     * @return the negotiation with the specified id or null if not found
     */
    Negotiation findById(int id);
    
    /**
     * finds a negotiation between a specific offer and request
     * @param singOffer the sing offer involved in the negotiation
     * @param singRequest the sing request involved in the negotiation
     * @return the negotiation between the specified offer and request or null if not found
     */
    Negotiation findBySingOfferAndSingRequest(SingOffer singOffer, SingRequest singRequest);
    
    /**
     * finds negotiations for a specific offer with the given acceptance status
     * @param singOffer the sing offer to search for
     * @param wasAccepted whether the negotiation was accepted or rejected
     * @return the negotiation matching the criteria or null if not found
     */
    // to see all negotiations that have been accepted or rejected (wasAccepted = true or false)
    Negotiation findBySingOfferAndWasAccepted(SingOffer singOffer, boolean wasAccepted);
    
    /**
     * finds negotiations for a specific request with the given acceptance status
     * @param singRequest the sing request to search for
     * @param wasAccepted whether the negotiation was accepted or rejected
     * @return the negotiation matching the criteria or null if not found
     */
    Negotiation findBySingRequestAndWasAccepted(SingRequest singRequest, boolean wasAccepted);
    
    /**
     * finds open negotiations for a specific offer
     * @param singOffer the sing offer to search for
     * @return unclosed negotiations for the specified offer or null if not found
     */
    // to see negotiations that are still open
    Negotiation findBySingOfferAndTsClosureIsNull(SingOffer singOffer);
    
    /**
     * finds open negotiations for a specific request
     * @param singRequest the sing request to search for
     * @return unclosed negotiations for the specified request or null if not found
     */
    Negotiation findBySingRequestAndTsClosureIsNull(SingRequest singRequest);
    
    /**
     * retrieves all negotiations
     * @return list of all negotiations
     */
    @NonNull List<Negotiation> findAll();

    /**
     * deletes all negotiations associated with a specific offer
     * @param singOffer the sing offer whose negotiations should be deleted
     */
    void deleteBySingOffer(SingOffer singOffer);
    
    /**
     * deletes all negotiations associated with a specific request
     * @param singRequest the sing request whose negotiations should be deleted
     */
    void deleteBySingRequest(SingRequest singRequest);
}
