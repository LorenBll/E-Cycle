package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Brands_Repository;
import ecycle.ecycle.models.Brand;
import java.util.List;

/**
 * Service class for handling brand operations.
 */
@Service
@RequiredArgsConstructor
public class Brands_Service {
        
    private final Brands_Repository brandsRepository;

    /**
     * finds a brand by its ID.
     * 
     * @param id the ID of the brand
     * @return the brand if found, null otherwise
     */
    public Brand findById(String id) {
        return brandsRepository.findById(id).orElse(null);
    }

    /**
     * retrieves all brands from the database.
     * 
     * @return a list of all brands
     */
    public List<Brand> findAll () {
        return brandsRepository.findAll();
    }

    /**
     * saves a brand to the database.
     * 
     * @param brand the brand to save
     * @return the saved brand with updated information
     */
    public Brand save(Brand brand) {
        return brandsRepository.save(brand);
    }   

}
