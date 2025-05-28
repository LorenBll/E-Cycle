package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Negotiation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ecycle.ecycle.models.SingOffer;
import ecycle.ecycle.models.SingRequest;
import org.springframework.lang.NonNull;

@Repository
public interface Negotiations_Repository extends JpaRepository<Negotiation, Integer> {

    Negotiation findById(int id);
    List<Negotiation> findBySingOffer(SingOffer singOffer);
    List<Negotiation> findBySingRequest(SingRequest singRequest);
    List<Negotiation> findBySingOfferAndSingRequest(SingOffer singOffer, SingRequest singRequest);
    // to see all negotiations that have been accepted or rejected (wasAccepted = true or false)
    Negotiation findBySingOfferAndWasAccepted(SingOffer singOffer, boolean wasAccepted);
    Negotiation findBySingRequestAndWasAccepted(SingRequest singRequest, boolean wasAccepted);
    // to see negotiations that are still open
    List<Negotiation> findBySingOfferAndTsClosureIsNull(SingOffer singOffer);
    List<Negotiation> findBySingRequestAndTsClosureIsNull(SingRequest singRequest);
    // to see all negotiations that have been closed (whether accepted or not)
    List<Negotiation> findBySingOfferAndTsClosureIsNotNull(SingOffer singOffer);
    List<Negotiation> findBySingRequestAndTsClosureIsNotNull(SingRequest singRequest);
    @NonNull List<Negotiation> findAll();

    void deleteById(int id);
    void deleteBySingOffer(SingOffer singOffer);
    void deleteBySingRequest(SingRequest singRequest);

}