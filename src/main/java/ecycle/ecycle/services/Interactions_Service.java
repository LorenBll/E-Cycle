package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Interactions_Repository;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import java.util.ArrayList;
import java.util.List;
import ecycle.ecycle.models.User;

/**
 * Service class for handling interaction operations.
 */
@Service
@RequiredArgsConstructor
public class Interactions_Service {
    
    private final Interactions_Repository interactionsRepository;
    private final SingRequests_Service singRequestsService;
    private final SingOffers_Service singOffersService;    
   
    /**
     * sorts interactions by their creation timestamp in descending order.
     * this method is still used internally by findByUserAndIsOfferAndIsActive.
     * 
     * @param interactions the list of interactions to sort
     * @return the sorted list of interactions
     */
    private List<Interaction> sort(List<Interaction> interactions) {
        // sort by tsCreation in descending order (newest first)
        interactions.sort((i1, i2) -> i2.getTsCreation().compareTo(i1.getTsCreation()));
        return interactions;
    }

    /**
     * checks if an interaction is active by examining its associated sing offers or requests.
     * an interaction is active if at least one of its associated sing offers/requests is active.
     * 
     * @param interaction the interaction to check
     * @return true if the interaction is active, false otherwise
     */
    private boolean isInteractionActive(Interaction interaction) {
        
        // check if the interaction is active
        boolean isThereAnyActive = false;
        
        if (interaction.getIsOffer()) {
            // for offers, check if any associated sing offers are active
            List<SingOffer> singOffers = singOffersService.findByOffer(interaction);
            for (SingOffer singOffer : singOffers) {
                if (singOffersService.isActive(singOffer)) {
                    isThereAnyActive = true;
                    break; // no need to check further, we found an active sing offer
                }
            }
        } else {
            // for requests, check if any associated sing requests are active
            List<SingRequest> singRequests = singRequestsService.findByRequest(interaction);
            for (SingRequest singRequest : singRequests) {
                if (singRequestsService.isActive(singRequest)) {
                    isThereAnyActive = true;
                    break; // no need to check further, we found an active sing request
                }
            }            
        }
        
        return isThereAnyActive;
    }

    /**
     * finds an interaction by its ID.
     * 
     * @param id the ID of the interaction
     * @return the interaction if found, null otherwise
     */
    public Interaction findById(int id) {
        return interactionsRepository.findById(id);
    }

    /**
     * finds interactions by user, type (offer/request), and activity status.
     * 
     * @param user the user associated with the interactions
     * @param isOffer true to find offers, false to find requests
     * @param isActive true to find active interactions, false to find inactive ones
     * @return a sorted list of interactions matching the criteria
     */
    public List<Interaction> findByUserAndIsOfferAndIsActive(User user, boolean isOffer, boolean isActive) {
        
        // get all interactions for the user of the specified type
        List<Interaction> rawInteractions = interactionsRepository.findByUserAndIsOffer(user, isOffer);
        List<Interaction> requestedInteractions = new ArrayList<>(rawInteractions);

        // filter based on activity status
        for (Interaction interaction : rawInteractions) {
            if (isActive && !this.isInteractionActive(interaction)) {
                // remove interactions that are not active when we want active ones
                requestedInteractions.remove(interaction);
            }
            else if (!isActive && this.isInteractionActive(interaction)) {
                // remove interactions that are active when we want inactive ones
                requestedInteractions.remove(interaction);
            }
        }
        return this.sort(requestedInteractions);
    }

    /**
     * saves an interaction to the database.
     * 
     * @param interaction the interaction to save
     * @return the saved interaction with updated information
     */
    public Interaction save(Interaction interaction) {
        return interactionsRepository.save(interaction);
    }

}

