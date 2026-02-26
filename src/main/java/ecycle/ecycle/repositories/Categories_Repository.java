package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * repository interface for category entity operations
 */
@Repository
public interface Categories_Repository extends JpaRepository<Category, String> {

    /**
     * finds a category by its id
     * @param id the id to search for
     * @return an optional containing the category if found
     */
    @NonNull Optional<Category> findById(@NonNull String id);
    
    /**
     * retrieves all categories
     * @return list of all categories
     */
    @NonNull List<Category> findAll();
}
