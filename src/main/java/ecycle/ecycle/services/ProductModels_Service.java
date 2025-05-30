package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.ProductModels_Repository;
import ecycle.ecycle.models.ProductModel;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductModels_Service {

    private final ProductModels_Repository modelsRepository;

    public ProductModel findById(String id) {
        return modelsRepository.findById(id).orElse(null);
    }
    
    public List<ProductModel> findAll () {
        return modelsRepository.findAll();
    }
    
    // register a new model
    public ProductModel save(ProductModel model) {
        return modelsRepository.save(model);
    }

}
