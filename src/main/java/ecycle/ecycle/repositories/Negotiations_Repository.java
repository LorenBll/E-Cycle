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
    List<Negotiation> findBySingOfferAndSingRequest(SingOffer singOffer, SingRequest singRequest);
    // to see all negotiations that have been accepted or rejected (wasAccepted = true or false)
    Negotiation findBySingOfferAndWasAccepted(SingOffer singOffer, boolean wasAccepted);
    Negotiation findBySingRequestAndWasAccepted(SingRequest singRequest, boolean wasAccepted);
    // to see negotiations that are still open
    Negotiation findBySingOfferAndTsClosureIsNull(SingOffer singOffer);
    Negotiation findBySingRequestAndTsClosureIsNull(SingRequest singRequest);
    @NonNull List<Negotiation> findAll();

    void deleteBySingOffer(SingOffer singOffer);
    void deleteBySingRequest(SingRequest singRequest);

}
