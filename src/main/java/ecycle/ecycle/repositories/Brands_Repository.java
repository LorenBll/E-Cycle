package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * repository interface for brand entity operations
 */
@Repository
public interface Brands_Repository extends JpaRepository<Brand, String> {

    /**
     * finds a brand by its id
     * @param id the id to search for
     * @return an optional containing the brand if found
     */
    @NonNull Optional<Brand> findById(@NonNull String id);
    
    /**
     * retrieves all brands
     * @return list of all brands
     */
    @NonNull List<Brand> findAll();
}
