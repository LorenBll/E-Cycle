package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import ecycle.ecycle.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface Brands_Repository extends JpaRepository<Brand, String> {

    @NonNull Optional<Brand> findById(String id);
    @NonNull List<Brand> findAll();
 
}