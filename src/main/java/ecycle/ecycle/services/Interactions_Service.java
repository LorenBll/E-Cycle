package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ecycle.ecycle.repositories.Interactions_Repository;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import java.util.ArrayList;
import java.util.List;
import ecycle.ecycle.models.User;

@Service
@RequiredArgsConstructor
public class Interactions_Service {
    
    private final Interactions_Repository interactionsRepository;
    private final SingRequests_Service singRequestsService;
    private final SingOffers_Service singOffersService;
    

    private List<Interaction> sort ( List<Interaction> interactions) {
        // sort by tsCreation
        interactions.sort((i1, i2) -> i2.getTsCreation().compareTo(i1.getTsCreation()));
        return interactions;
    }

    private boolean isInteractionActive(Interaction interaction) {
        // check if the interaction is active
        
        if (interaction.getIsOffer()) {

            List<SingOffer> singOffers = singOffersService.findByOffer(interaction);
            boolean isThereAnyActive = false;
            for (SingOffer singOffer : singOffers) {
                if (singOffersService.isSingOfferActive(singOffer)) {
                    isThereAnyActive = true;
                    break;
                }
            }

            return isThereAnyActive;

        }
        else {
            
            List<SingRequest> singRequests = singRequestsService.findByRequest(interaction);
            boolean isThereAnyActive = false;
            for (SingRequest singRequest : singRequests) {
                if (singRequestsService.isSingRequestActive(singRequest)) {
                    isThereAnyActive = true;
                    break;
                }
            }

            return isThereAnyActive;

        }

    }

    public Interaction findById(int id) {
        return interactionsRepository.findById(id);
    }

    public List<Interaction> findByUserAndIsOfferAndIsActive(User user, boolean isOffer , boolean isActive) {
        
        List<Interaction> rawInteractions =  interactionsRepository.findByUserAndIsOffer(user, isOffer);
        List<Interaction> requestedInteractions = new ArrayList<>(rawInteractions);

        for (Interaction interaction : rawInteractions) {
            if (isActive && !this.isInteractionActive(interaction)) {
                requestedInteractions.remove(interaction);
            }
            else if (!isActive && this.isInteractionActive(interaction)) {
                requestedInteractions.remove(interaction);
            }
        }
        return this.sort(requestedInteractions);
    }

    public List<Interaction> findAll() {
        List<Interaction> interactions = interactionsRepository.findAll();
        return this.sort(interactions);
    }

    // register a new interaction
    public Interaction save(Interaction interaction) {
        return interactionsRepository.save(interaction);
    }    // delete interaction
    @Transactional
    public void delete(Interaction interaction) {
        interactionsRepository.deleteById(interaction.getId());
    }
      
    // delete interaction by user
    @Transactional
    public void deleteByUser(User user) {
        // find by user
        List<Interaction> interactions = interactionsRepository.findByUser(user);
        for (Interaction interaction : interactions) {
            if (interaction.getIsOffer()) {
                // delete all sing requests
                singRequestsService.deleteByRequest(interaction);
            }
            else {
                // delete all sing requests
                singRequestsService.deleteByRequest(interaction);
            }
        }
        // delete all interactions
        interactionsRepository.deleteByUser(user);
    }

}
