package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Nature;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * repository interface for nature entity operations
 */
@Repository
public interface Natures_Repository extends JpaRepository<Nature, String> {

    /**
     * finds a nature by its id
     * @param id the id to search for
     * @return an optional containing the nature if found
     */
    @NonNull Optional<Nature> findById(@NonNull String id);
    
    /**
     * retrieves all natures
     * @return list of all natures
     */
    @NonNull List<Nature> findAll();
}
