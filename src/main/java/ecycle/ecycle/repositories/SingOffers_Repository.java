package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.SingOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.Characteristics;
import java.sql.Date;

@Repository
public interface SingOffers_Repository extends JpaRepository<SingOffer, Integer> {

    SingOffer findById(int id);
    List<SingOffer> findByOffer(Interaction offer);
    List<SingOffer> findByOfferAndTsDeletionIsNull(Interaction offer);
    List<SingOffer> findByOfferAndTsDeletionIsNotNull(Interaction offer);
    List<SingOffer> findByTsDeletionIsNullAndExpirationAfterAndCharacteristics(Date expiration, Characteristics characteristics);
    @NonNull List<SingOffer> findAll();
    
    void deleteByOffer(Interaction offer);

}