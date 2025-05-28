package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Nature;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface Natures_Repository extends JpaRepository<Nature, String> {

    @NonNull Optional<Nature> findById(@NonNull String id);
    @NonNull List<Nature> findAll();
    
}