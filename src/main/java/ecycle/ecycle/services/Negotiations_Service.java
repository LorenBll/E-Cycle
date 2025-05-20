package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Negotiations_Repository;
import ecycle.ecycle.models.Negotiation;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Negotiations_Service {
        
    private final Negotiations_Repository negotiationsRepository;

    public Negotiation findById(int id) {
        return negotiationsRepository.findById(id);
    }

    public List<Negotiation> findBySingOffer(SingOffer singOffer) {
        return negotiationsRepository.findBySingOffer(singOffer);
    }

    public List<Negotiation> findBySingRequest(SingRequest singRequest) {
        return negotiationsRepository.findBySingRequest(singRequest);
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

    public void deleteBySingOffer(SingOffer singOffer) {
        negotiationsRepository.deleteBySingOffer(singOffer);
    }

    public void deleteBySingRequest(SingRequest singRequest) {
        negotiationsRepository.deleteBySingRequest(singRequest);
    }

}