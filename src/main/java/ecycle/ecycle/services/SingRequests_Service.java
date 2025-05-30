package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ecycle.ecycle.repositories.SingRequests_Repository;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingRequest;
import java.util.List;
import ecycle.ecycle.models.Negotiation;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SingRequests_Service {
    
    private final SingRequests_Repository singRequestsRepository;
    private final Negotiations_Service negotiationsService;

    public boolean isSingRequestActive(SingRequest singRequest) {
        
        // check if the sing request has been deleted
        boolean isActive = (
            singRequest.getTsDeletion() == null
        );

        // check if there are any negotiations with the request that have been accepted
        if (isActive) {

            Negotiation negotiation = negotiationsService.findBySingRequestAndWasAccepted(singRequest , true);
            if (negotiation != null) {
                isActive = false;
            }

        }

        return isActive;

    }
    
    public SingRequest findById(int id) {
        return singRequestsRepository.findById(id);
    }

    public List<SingRequest> findByRequest(Interaction request) {
        return singRequestsRepository.findByRequest(request);
    }

    public List<SingRequest> findAll() {
        return singRequestsRepository.findAll();
    }

    public SingRequest save(SingRequest singRequest) {
        return singRequestsRepository.save(singRequest);
    }

    public void delete(SingRequest singRequest) {
        singRequest.setTsDeletion(new Timestamp(System.currentTimeMillis()));
        singRequestsRepository.save(singRequest);
    }    
    
    @Transactional
    public void deleteByRequest(Interaction request) {
        // find by request
        List<SingRequest> singRequests = singRequestsRepository.findByRequest(request);
        // delete negotiations
        for (SingRequest singRequest : singRequests) {
            negotiationsService.deleteBySingRequest(singRequest);
        }
        // delete all sing requests
        singRequestsRepository.deleteByRequest(request);
    }

}

