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

@Service
@RequiredArgsConstructor
public class SingOffers_Service {
    
    private final SingOffers_Repository singOffersRepository;
    private final Negotiations_Service negotiationsService;

    public boolean isSingOfferActive(SingOffer singOffer) {
        
        // check if the sing offer has been deleted or has expired
        boolean isActive = (
            singOffer.getTsDeletion() == null && 
            singOffer.getExpiration().after(new Date(System.currentTimeMillis()))
        );

        // check if there are any negotiations with the offer that have been accepted
        if (isActive) {
        
            Negotiation negotiation = negotiationsService.findBySingOfferAndWasAccepted(singOffer , true);
            if (negotiation != null) {
                isActive = false;
            }

        }

        return isActive;

    }
    
    public SingOffer findById(int id) {
        return singOffersRepository.findById(id);
    }

    public List<SingOffer> findByOffer(Interaction offer) {
        return singOffersRepository.findByOffer(offer);
    }

    public List<SingOffer> findAll () {
        return singOffersRepository.findAll();
    }

    public SingOffer save(SingOffer singOffer) {
        return singOffersRepository.save(singOffer);
    }

    @Transactional
    public void delete(SingOffer singOffer) {
        singOffer.setTsDeletion(new Timestamp(System.currentTimeMillis()));
        singOffersRepository.save(singOffer);
    }

    @Transactional
    public void deleteByOffer(Interaction offer) {
        // find by offer
        List<SingOffer> singOffers = singOffersRepository.findByOffer(offer);
        // delete negotiations
        for (SingOffer singOffer : singOffers) {
            negotiationsService.deleteBySingOffer(singOffer);
        }
        // delete all sing offers
        singOffersRepository.deleteByOffer(offer);
    }

}

