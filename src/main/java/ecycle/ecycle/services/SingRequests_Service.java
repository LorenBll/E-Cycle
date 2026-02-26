package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.SingRequests_Repository;
import ecycle.ecycle.models.Interaction;
import ecycle.ecycle.models.SingRequest;
import java.util.List;
import ecycle.ecycle.models.Characteristics;
import ecycle.ecycle.models.User;
import ecycle.ecycle.models.Negotiation;
import java.sql.Timestamp;

/**
 * Service class for handling sing request operations.
 */
@Service
@RequiredArgsConstructor
public class SingRequests_Service {
    
    private final SingRequests_Repository singRequestsRepository;
    private final Negotiations_Service negotiationsService;

    /**
     * checks if a sing request is currently active.
     * a sing request is active if it hasn't been deleted, the user exists, and it hasn't been accepted.
     * 
     * @param singRequest the sing request to check
     * @return true if the sing request is active, false otherwise
     */
    public boolean isActive(SingRequest singRequest) {
        
        // check if the sing request has been deleted or if the user exists
        boolean isDeleted = (singRequest.getTsDeletion() != null);
        boolean userExists = (singRequest.getRequest().getUser() != null);

        // check if the sing request has been accepted
        boolean isAccepted;
        Negotiation negotiation = negotiationsService.findBySingRequestAndWasAccepted(singRequest, true);
        if (negotiation == null) {
            isAccepted = false;
        } else {
            isAccepted = true;
        }

        return (!isDeleted && userExists && !isAccepted);

    }

    /**
     * checks if a sing request is available for negotiation.
     * a sing request is available if it is active and doesn't have a pending negotiation.
     * 
     * @param singRequest the sing request to check
     * @return true if the sing request is available for negotiation, false otherwise
     */
    public boolean isntDeletedAndUserExistsAndIsntAcceptedAndIsntPending(SingRequest singRequest) {
        
        // check if the sing request has been deleted or if the user exists
        boolean isActive = isActive(singRequest);

        // check if the sing request has a pending negotiation
        boolean isPending;
        Negotiation pendingNegotiation = negotiationsService.findBySingRequestAndTsClosureIsNull(singRequest);
        if (pendingNegotiation == null) {
            isPending = false;
        } else {
            isPending = true;
        }

        return (isActive && !isPending);
    
    }
    
    /**
     * finds a sing request by its ID.
     * 
     * @param id the ID of the sing request
     * @return the sing request if found, null otherwise
     */
    public SingRequest findById(int id) {
        return singRequestsRepository.findById(id);
    }

    /**
     * finds all sing requests associated with a specific interaction request.
     * 
     * @param request the interaction request to search for
     * @return a list of sing requests associated with the interaction
     */
    public List<SingRequest> findByRequest(Interaction request) {
        return singRequestsRepository.findByRequest(request);
    }

    /**
     * finds sing requests that match specific criteria.
     * 
     * @param isAvailable indicates whether to find available or unavailable requests
     * @param characteristics the characteristics to match
     * @param user the user to exclude from results (don't show user's own requests)
     * @param price the maximum price to consider
     * @return a list of sing requests matching the given criteria
     */
    public List<SingRequest> findByAvailabilityAndCharacteristicsAndNotUserAndPrice (
        boolean isAvailable, 
        Characteristics characteristics, 
        User user, 
        float price
    ) {
        
        List<SingRequest> rawSingRequests = singRequestsRepository.findAll();
        
        // filter by availability status
        List<SingRequest> filteredSingRequests = rawSingRequests.stream()
            .filter(singRequest -> isntDeletedAndUserExistsAndIsntAcceptedAndIsntPending(singRequest) == isAvailable)
            .toList();

        // apply additional filters: matching characteristics, excluding user's own requests, and price constraints
        List<SingRequest> finalFilteredSingRequests = filteredSingRequests.stream()
            .filter(singRequest -> singRequest.getCharacteristics() != null && singRequest.getCharacteristics().getId() == characteristics.getId())
            .filter(singRequest -> singRequest.getRequest().getUser() != null && !(singRequest.getRequest().getUser().getId() == user.getId()))
            .filter(singRequest -> !Float.isNaN(singRequest.getMaxPrice()))
            .filter(singRequest -> singRequest.getMaxPrice() <= price)
            .toList();

        return finalFilteredSingRequests;

    }

    /**
     * retrieves all sing requests from the database.
     * 
     * @return a list of all sing requests
     */
    public List<SingRequest> findAll() {
        return singRequestsRepository.findAll();
    }

    /**
     * saves a sing request to the database.
     * 
     * @param singRequest the sing request to save
     * @return the saved sing request with updated information
     */
    public SingRequest save(SingRequest singRequest) {
        return singRequestsRepository.save(singRequest);
    }

    /**
     * marks a sing request as deleted by setting its deletion timestamp.
     * 
     * @param singRequest the sing request to delete
     */
    public void delete(SingRequest singRequest) {
        singRequest.setTsDeletion(new Timestamp(System.currentTimeMillis()));
        singRequestsRepository.save(singRequest);
    }    

}

