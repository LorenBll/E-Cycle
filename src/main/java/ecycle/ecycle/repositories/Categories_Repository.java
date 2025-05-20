package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface Categories_Repository extends JpaRepository<Category, String> {

    @NonNull Optional<Category> findById(String id);
    @NonNull List<Category> findAll();
  
}