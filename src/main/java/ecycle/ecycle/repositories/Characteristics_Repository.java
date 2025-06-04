package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Characteristics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;

/**
 * repository interface for characteristics entity operations
 */
@Repository
public interface Characteristics_Repository extends JpaRepository<Characteristics, Integer> {
    
    /* 
    there will be no other research methods for this entity, 
    as it would require the contemplation of all possible combinations of characteristics values
    */
    
    /**
     * retrieves all characteristics
     * @return list of all characteristics
     */
    @NonNull List<Characteristics> findAll();
}
