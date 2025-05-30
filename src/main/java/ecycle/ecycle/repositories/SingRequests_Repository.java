package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.SingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import ecycle.ecycle.models.Interaction;

@Repository
public interface SingRequests_Repository extends JpaRepository<SingRequest, Integer> {

    SingRequest findById (int id);
    List<SingRequest> findByRequest(Interaction request);
    @NonNull List<SingRequest> findAll();

    void deleteByRequest(Interaction request);

}
