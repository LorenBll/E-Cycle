package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import ecycle.ecycle.models.ProductModel;
import org.springframework.lang.NonNull;

/**
 * repository interface for product model entity operations
 */
@Repository
public interface ProductModels_Repository extends JpaRepository<ProductModel, String> {

    /**
     * finds a product model by its id
     * @param id the id to search for
     * @return an optional containing the product model if found
     */
    @NonNull Optional<ProductModel> findById(@NonNull String id);
    
    /**
     * retrieves all product models
     * @return list of all product models
     */
    @NonNull List<ProductModel> findAll();
}
