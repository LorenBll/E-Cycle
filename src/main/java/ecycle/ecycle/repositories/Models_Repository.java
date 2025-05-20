package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import ecycle.ecycle.models.Model;
import ecycle.ecycle.models.Brand;
import org.springframework.lang.NonNull;

@Repository
public interface Models_Repository extends JpaRepository<Model, String> {

    @NonNull Optional<Model> findById(String id);
    List<Model> findByBrand(Brand brand);
    @NonNull List<Model> findAll();

}