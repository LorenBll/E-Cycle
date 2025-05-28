package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ecycle.ecycle.repositories.Negotiations_Repository;
import ecycle.ecycle.models.Negotiation;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Negotiations_Service {
        
    private final Negotiations_Repository negotiationsRepository;

    public boolean isSingOfferActive(SingOffer singOffer) {
        
        // check if the sing offer has been deleted or has expired
        boolean isActive = (
            singOffer.getTsDeletion() == null && 
            singOffer.getExpiration().after(new Date(System.currentTimeMillis()))
        );

        // check if there are any negotiations with the offer that have been accepted
        if (isActive) {
        
            Negotiation negotiation = this.findBySingOfferAndWasAccepted(singOffer , true);
            if (negotiation != null) {
                isActive = false;
            }

        }

        return isActive;

    }

    public Negotiation findById(int id) {
        return negotiationsRepository.findById(id);
    }

    public List<Negotiation> findBySingOffer(SingOffer singOffer) {
        return negotiationsRepository.findBySingOffer(singOffer);
    }

    public List<Negotiation> findBySingRequest(SingRequest singRequest) {
        return negotiationsRepository.findBySingRequest(singRequest);
    }

    public List<Negotiation> findBySingOfferAndSingRequest(SingOffer singOffer, SingRequest singRequest) {
        return negotiationsRepository.findBySingOfferAndSingRequest(singOffer, singRequest);
    }

    public Negotiation findBySingOfferAndWasAccepted(SingOffer singOffer, boolean wasAccepted) {
        return negotiationsRepository.findBySingOfferAndWasAccepted(singOffer, wasAccepted);
    }

    public Negotiation findBySingRequestAndWasAccepted(SingRequest singRequest, boolean wasAccepted) {
        return negotiationsRepository.findBySingRequestAndWasAccepted(singRequest, wasAccepted);
    }

    public List<Negotiation> findBySingOfferAndTsClosureIsNull(SingOffer singOffer) {
        return negotiationsRepository.findBySingOfferAndTsClosureIsNull(singOffer);
    }

    public List<Negotiation> findBySingRequestAndTsClosureIsNull(SingRequest singRequest) {
        return negotiationsRepository.findBySingRequestAndTsClosureIsNull(singRequest);
    }

    public List<Negotiation> findBySingOfferAndTsClosureIsNotNull(SingOffer singOffer) {
        return negotiationsRepository.findBySingOfferAndTsClosureIsNotNull(singOffer);
    }

    public List<Negotiation> findBySingRequestAndTsClosureIsNotNull(SingRequest singRequest) {
        return negotiationsRepository.findBySingRequestAndTsClosureIsNotNull(singRequest);
    }

    public List<Negotiation> findAll() {
        return negotiationsRepository.findAll();
    }

    public void deleteById(int id) {
        negotiationsRepository.deleteById(id);
    }    
    
    @Transactional
    public void deleteBySingOffer(SingOffer singOffer) {
        negotiationsRepository.deleteBySingOffer(singOffer);
    }

    @Transactional
    public void deleteBySingRequest(SingRequest singRequest) {
        negotiationsRepository.deleteBySingRequest(singRequest);
    }

}