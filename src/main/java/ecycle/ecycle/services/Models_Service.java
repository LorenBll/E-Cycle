package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Models_Repository;
import ecycle.ecycle.models.Model;
import ecycle.ecycle.models.Brand;
import java.util.List;

@Service @RequiredArgsConstructor
public class Models_Service {

    private final Models_Repository modelsRepository;

    public Model findById(String id) {
        return modelsRepository.findById(id).orElse(null);
    }
    
    public List<Model> findByBrand(Brand brand) {
        return modelsRepository.findByBrand(brand);
    }
    
    public List<Model> findAll () {
        return modelsRepository.findAll();
    }
    
    // register a new model
    public Model save(Model model) {
        return modelsRepository.save(model);
    }

}