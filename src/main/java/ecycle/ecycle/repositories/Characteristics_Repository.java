package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Characteristics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.lang.NonNull;
import java.util.Optional;

@Repository
public interface Characteristics_Repository extends JpaRepository<Characteristics, Integer> {
    
    Characteristics findById(int id);
    /* 
    there will be no other research methods for this entity, 
    as it would require the contemplation of all possible combinations of characteristics values
    */
    @NonNull List<Characteristics> findAll();

}