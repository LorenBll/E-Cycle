package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Brands_Repository;
import ecycle.ecycle.models.Brand;
import java.util.List;

@Service @RequiredArgsConstructor
public class Brands_Service {
        
    private final Brands_Repository brandsRepository;

    public Brand findById(String id) {
        return brandsRepository.findById(id).orElse(null);
    }

    public List<Brand> findAll () {
        return brandsRepository.findAll();
    }

    // register a new brand
    public Brand save(Brand brand) {
        return brandsRepository.save(brand);
    }   

}