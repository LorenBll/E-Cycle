package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.ProductModels_Repository;
import ecycle.ecycle.models.ProductModel;
import java.util.List;

/**
 * Service class for handling product model operations.
 */
@Service
@RequiredArgsConstructor
public class ProductModels_Service {

    private final ProductModels_Repository modelsRepository;

    /**
     * finds a product model by its ID.
     * 
     * @param id the ID of the product model
     * @return the product model if found, null otherwise
     */
    public ProductModel findById(String id) {
        return modelsRepository.findById(id).orElse(null);
    }
    
    /**
     * retrieves all product models from the database.
     * 
     * @return a list of all product models
     */
    public List<ProductModel> findAll () {
        return modelsRepository.findAll();
    }
    
    /**
     * registers a new product model in the database.
     * 
     * @param model the product model to save
     * @return the saved product model with updated information
     */
    public ProductModel save(ProductModel model) {
        return modelsRepository.save(model);
    }

}
