package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Interactions_Repository;
import ecycle.ecycle.models.Interaction;
import java.util.List;
import ecycle.ecycle.models.User;

@Service @RequiredArgsConstructor
public class Interactions_Service {
    
    private final Interactions_Repository interactionsRepository;
    private final SingRequests_Service singRequestsService;
    private final SingOffers_Service singOffersService;

    private List<Interaction> sort ( List<Interaction> interactions) {
        // sort by tsCreation
        interactions.sort((i1, i2) -> i2.getTsCreation().compareTo(i1.getTsCreation()));
        return interactions;
    }

    public Interaction findById(int id) {
        return interactionsRepository.findById(id);
    }

    public List<Interaction> findByUserAndIsOffer(User user, boolean isOffer) {
        List<Interaction> interactions =  interactionsRepository.findByUserAndIsOffer(user, isOffer);
        return this.sort(interactions);
    }

    public List<Interaction> findAll() {
        List<Interaction> interactions = interactionsRepository.findAll();
        return this.sort(interactions);
    }

    // register a new interaction
    public Interaction save(Interaction interaction) {
        return interactionsRepository.save(interaction);
    }

    // delete interaction
    public void delete(Interaction interaction) {
        interactionsRepository.deleteById(interaction.getId());
    }
    
    // delete interaction by user
    public void deleteByUser(User user) {
        // find by user
        List<Interaction> interactions = interactionsRepository.findByUser(user);
        for (Interaction interaction : interactions) {
            if (!interaction.isOffer()) {
                // delete all sing requests
                singRequestsService.deleteByRequest(interaction);
            }
            else {
                // delete all sing offers
                singOffersService.deleteByOffer(interaction);
            }
        }
        // delete all interactions
        interactionsRepository.deleteByUser(user);
    }

}
