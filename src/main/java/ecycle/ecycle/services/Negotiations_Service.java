package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ecycle.ecycle.repositories.Negotiations_Repository;
import ecycle.ecycle.models.Negotiation;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import java.util.List;

/**
 * Service class for handling negotiation operations.
 */
@Service
@RequiredArgsConstructor
public class Negotiations_Service {
        
    private final Negotiations_Repository negotiationsRepository;
    
    /**
     * finds a negotiation by its ID.
     * this method is used in routineCheck() method of AppController.
     * 
     * @param id the ID of the negotiation
     * @return the negotiation if found, null otherwise
     */
    public Negotiation findById(int id) {
        return negotiationsRepository.findById(id);
    }
    
    /**
     * finds a negotiation by its associated sing offer and sing request.
     * 
     * @param singOffer the sing offer associated with the negotiation
     * @param singRequest the sing request associated with the negotiation
     * @return the negotiation if found, null otherwise
     */
    public Negotiation findBySingOfferAndSingRequest(SingOffer singOffer, SingRequest singRequest) {
        return negotiationsRepository.findBySingOfferAndSingRequest(singOffer, singRequest);
    }

    /**
     * finds a negotiation by its sing offer and acceptance status.
     * 
     * @param singOffer the sing offer associated with the negotiation
     * @param wasAccepted the acceptance status to look for
     * @return the negotiation if found, null otherwise
     */
    public Negotiation findBySingOfferAndWasAccepted(SingOffer singOffer, boolean wasAccepted) {
        return negotiationsRepository.findBySingOfferAndWasAccepted(singOffer, wasAccepted);
    }

    /**
     * finds a negotiation by its sing request and acceptance status.
     * 
     * @param singRequest the sing request associated with the negotiation
     * @param wasAccepted the acceptance status to look for
     * @return the negotiation if found, null otherwise
     */
    public Negotiation findBySingRequestAndWasAccepted(SingRequest singRequest, boolean wasAccepted) {
        return negotiationsRepository.findBySingRequestAndWasAccepted(singRequest, wasAccepted);
    }

    /**
     * finds a pending negotiation (without closure timestamp) by its sing offer.
     * 
     * @param singOffer the sing offer associated with the negotiation
     * @return the pending negotiation if found, null otherwise
     */
    public Negotiation findBySingOfferAndTsClosureIsNull(SingOffer singOffer) {
        return negotiationsRepository.findBySingOfferAndTsClosureIsNull(singOffer);
    }

    /**
     * finds a pending negotiation (without closure timestamp) by its sing request.
     * 
     * @param singRequest the sing request associated with the negotiation
     * @return the pending negotiation if found, null otherwise
     */
    public Negotiation findBySingRequestAndTsClosureIsNull(SingRequest singRequest) {
        return negotiationsRepository.findBySingRequestAndTsClosureIsNull(singRequest);
    }

    /**
     * retrieves all negotiations from the database.
     * 
     * @return a list of all negotiations
     */
    public List<Negotiation> findAll() {
        return negotiationsRepository.findAll();
    }

    /**
     * saves a negotiation to the database.
     * 
     * @param negotiation the negotiation to save
     * @return the saved negotiation with updated information
     */
    public Negotiation save(Negotiation negotiation) {
        return negotiationsRepository.save(negotiation);
    }

    /**
     * deletes all negotiations associated with a specific sing offer.
     * 
     * @param singOffer the sing offer whose negotiations should be deleted
     */
    @Transactional
    public void deleteBySingOffer(SingOffer singOffer) {
        negotiationsRepository.deleteBySingOffer(singOffer);
    }

    /**
     * deletes all negotiations associated with a specific sing request.
     * 
     * @param singRequest the sing request whose negotiations should be deleted
     */
    @Transactional
    public void deleteBySingRequest(SingRequest singRequest) {
        negotiationsRepository.deleteBySingRequest(singRequest);
    }

}
