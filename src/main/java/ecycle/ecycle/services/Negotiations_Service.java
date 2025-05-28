package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ecycle.ecycle.repositories.Negotiations_Repository;
import ecycle.ecycle.models.Negotiation;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Negotiations_Service {
        
    private final Negotiations_Repository negotiationsRepository;
    private final SingOffers_Service singOffersService;
    private final SingRequests_Service singRequestsService;

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

    public void lookFor_possibleNegotiations() {
        
        List<SingOffer> singOffers = singOffersService.findAll();
        List<SingRequest> singRequests = singRequestsService.findAll();

        /* 
         * in the research for possible negotiation, the program searches for
         * - singOffers and singNegotiations
        */

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