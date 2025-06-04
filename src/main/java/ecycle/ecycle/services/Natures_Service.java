package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Natures_Repository;
import ecycle.ecycle.models.Nature;
import java.util.List;

/**
 * Service class for handling nature operations.
 */
@Service
@RequiredArgsConstructor
public class Natures_Service {
    
    private final Natures_Repository natures_repository;
    
    /**
     * finds a nature by its ID.
     * 
     * @param id the ID of the nature
     * @return the nature if found, null otherwise
     */
    public Nature findById(String id) {
        return natures_repository.findById(id).orElse(null);
    }

    /**
     * retrieves all natures from the database.
     * 
     * @return a list of all natures
     */
    public List<Nature> findAll() {
        return natures_repository.findAll();
    }
    
    /**
     * saves a nature to the database.
     * 
     * @param nature the nature to save
     */
    public void save(Nature nature) {
        natures_repository.save(nature);
    }

}
