package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.SingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.Characteristics;

@Repository
public interface SingRequests_Repository extends JpaRepository<SingRequest, Integer> {

    SingRequest findById (int id);
    List<SingRequest> findByRequest(Interaction request);
    List<SingRequest> findByRequestAndTsDeletionIsNull(Interaction request);
    List<SingRequest> findByRequestAndTsDeletionIsNotNull(Interaction request);
    List<SingRequest> findByTsDeletionIsNullAndCharacteristics(Characteristics characteristics);
    @NonNull List<SingRequest> findAll();

    void deleteByRequest(Interaction request);

}