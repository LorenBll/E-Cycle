package ecycle.ecycle.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import ecycle.ecycle.models.ProductModel;
import org.springframework.lang.NonNull;

@Repository
public interface ProductModels_Repository extends JpaRepository<ProductModel, String> {

    @NonNull Optional<ProductModel> findById(@NonNull String id);
    @NonNull List<ProductModel> findAll();

}
