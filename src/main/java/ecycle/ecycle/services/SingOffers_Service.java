package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.SingOffers_Repository;
import org.springframework.transaction.annotation.Transactional;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingOffer;
import java.util.List;
import ecycle.ecycle.models.Negotiation;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Service class for handling sing offer operations.
 */
@Service
@RequiredArgsConstructor
public class SingOffers_Service {
    
    private final SingOffers_Repository singOffersRepository;
    private final Negotiations_Service negotiationsService;

    /**
     * checks if a sing offer is currently active.
     * an offer is active if it hasn't been deleted, hasn't expired, the user exists, and it hasn't been accepted.
     * 
     * @param singOffer the sing offer to check
     * @return true if the sing offer is active, false otherwise
     */
    public boolean isActive(SingOffer singOffer) {
        
        // check if the sing offer has been deleted or has expired or if the user exists
        boolean isDeleted = (singOffer.getTsDeletion() != null);
        boolean isExpired = (singOffer.getExpiration().before(new Date(System.currentTimeMillis())));
        boolean userExists = (singOffer.getOffer().getUser() != null);

        // check if the sing offer has been accepted
        boolean isAccepted;
        Negotiation negotiation = negotiationsService.findBySingOfferAndWasAccepted(singOffer, true);
        if (negotiation == null) {
            isAccepted = false;
        } else {
            isAccepted = true;
        }

        return (!isDeleted && !isExpired && userExists && !isAccepted);
        
    }

    /**
     * checks if a sing offer is available for negotiation.
     * an offer is available if it is active and doesn't have a pending negotiation.
     * 
     * @param singOffer the sing offer to check
     * @return true if the sing offer is available for negotiation, false otherwise
     */
    public boolean isntDeletedAndIsntExpiredAndUserExistsAndIsntAcceptedAndIsntPending(SingOffer singOffer) {
        
        // check if the sing offer is active
        boolean isActive = isActive(singOffer);

        // check if the sing offer has a pending negotiation
        boolean isPending;
        Negotiation pendingNegotiation = negotiationsService.findBySingOfferAndTsClosureIsNull(singOffer);
        if (pendingNegotiation == null) {
            isPending = false;
        } else {
            isPending = true;
        }

        return (isActive && !isPending);

    }
    
    /**
     * finds a sing offer by its ID.
     * 
     * @param id the ID of the sing offer
     * @return the sing offer if found, null otherwise
     */
    public SingOffer findById(int id) {
        return singOffersRepository.findById(id);
    }

    /**
     * finds all sing offers associated with a specific interaction offer.
     * 
     * @param offer the interaction offer to search for
     * @return a list of sing offers associated with the interaction
     */
    public List<SingOffer> findByOffer(Interaction offer) {
        return singOffersRepository.findByOffer(offer);
    }

    /**
     * finds sing offers based on their availability status.
     * 
     * @param isAvaialable indicates whether to find available or unavailable offers
     * @return a list of sing offers with the specified availability status
     */
    public List<SingOffer> findByAvailability(boolean isAvaialable) {
        
        List<SingOffer> rawSingOffers = singOffersRepository.findAll();
        // filter offers based on their availability status
        List<SingOffer> filteredSingOffers = rawSingOffers.stream()
            .filter(singOffer -> isntDeletedAndIsntExpiredAndUserExistsAndIsntAcceptedAndIsntPending(singOffer) == isAvaialable)
            .toList();
        return filteredSingOffers;

    }

    /**
     * retrieves all sing offers from the database.
     * 
     * @return a list of all sing offers
     */
    public List<SingOffer> findAll() {
        return singOffersRepository.findAll();
    }

    /**
     * saves a sing offer to the database.
     * 
     * @param singOffer the sing offer to save
     * @return the saved sing offer with updated information
     */
    public SingOffer save(SingOffer singOffer) {
        return singOffersRepository.save(singOffer);
    }

    /**
     * marks a sing offer as deleted by setting its deletion timestamp.
     * 
     * @param singOffer the sing offer to delete
     */
    @Transactional
    public void delete(SingOffer singOffer) {
        singOffer.setTsDeletion(new Timestamp(System.currentTimeMillis()));
        singOffersRepository.save(singOffer);
    }

}

