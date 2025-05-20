package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.SingRequests_Repository;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingRequest;
import java.util.List;
import ecycle.ecycle.models.Characteristics;
import java.sql.Timestamp;

@Service @RequiredArgsConstructor
public class SingRequests_Service {
    
    private final SingRequests_Repository singRequestsRepository;
    private final Negotiations_Service negotiationsService;

    public SingRequest findById(int id) {
        return singRequestsRepository.findById(id);
    }

    public List<SingRequest> findByRequest(Interaction request) {
        return singRequestsRepository.findByRequest(request);
    }

    public List<SingRequest> findByRequestAndTsDeletionIsNull(Interaction request) {
        return singRequestsRepository.findByRequestAndTsDeletionIsNull(request);
    }

    public List<SingRequest> findByRequestAndTsDeletionIsNotNull(Interaction request) {
        return singRequestsRepository.findByRequestAndTsDeletionIsNotNull(request);
    }

    public List<SingRequest> findByTsDeletionIsNullAndCharacteristicsAndMaxPriceMoreThan(Characteristics characteristics, float price) {
        List<SingRequest> singRequests = singRequestsRepository.findByTsDeletionIsNullAndCharacteristics(characteristics);
        singRequests.removeIf(singRequest -> singRequest.getMaxPrice() < price);
        return singRequests;
    }

    public List<SingRequest> findAll() {
        return singRequestsRepository.findAll();
    }

    // register a new sing request
    public SingRequest save(SingRequest singRequest) {
        return singRequestsRepository.save(singRequest);
    }

    // delete sing request (soft delete, update tsDeletion)
    public void delete(SingRequest singRequest) {
        singRequest.setTsDeletion(new Timestamp(System.currentTimeMillis()));
        singRequestsRepository.save(singRequest);
    }

    // delete sing request by request (hard delete)
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
