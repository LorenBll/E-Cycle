package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.SingOffers_Repository;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingOffer;
import java.util.List;
import ecycle.ecycle.models.Characteristics;
import java.sql.Date;
import java.sql.Timestamp;

@Service @RequiredArgsConstructor
public class SingOffers_Service {
    
    private final SingOffers_Repository singOffersRepository;

    public SingOffer findById(int id) {
        return singOffersRepository.findById(id);
    }

    public List<SingOffer> findByOffer(Interaction offer) {
        return singOffersRepository.findByOffer(offer);
    }

    public List<SingOffer> findByOfferAndTsDeletionIsNull(Interaction offer) {
        return singOffersRepository.findByOfferAndTsDeletionIsNull(offer);
    }

    public List<SingOffer> findByOfferAndTsDeletionIsNotNull(Interaction offer) {
        return singOffersRepository.findByOfferAndTsDeletionIsNotNull(offer);
    }

    public List<SingOffer> findByTsDeletionIsNullAndExpirationAfterAndCharacteristics(Date expiration, Characteristics characteristics) {
        return singOffersRepository.findByTsDeletionIsNullAndExpirationAfterAndCharacteristics(expiration, characteristics);
    }

    public List<SingOffer> findAll () {
        return singOffersRepository.findAll();
    }

    // register a new sing offer
    public SingOffer save(SingOffer singOffer) {
        return singOffersRepository.save(singOffer);
    }

    // delete sing offer (soft delete, update tsDeletion)
    public void delete(SingOffer singOffer) {
        singOffer.setTsDeletion(new Timestamp(System.currentTimeMillis()));
        singOffersRepository.save(singOffer);
    }

    // delete sing offer by offer (hard delete)
    public void deleteByOffer(Interaction offer) {
        // find by offer
        List<SingOffer> singOffers = singOffersRepository.findByOffer(offer);
        // todo delete negotiations

        // delete all sing offers
        singOffersRepository.deleteByOffer(offer);
    }

}
