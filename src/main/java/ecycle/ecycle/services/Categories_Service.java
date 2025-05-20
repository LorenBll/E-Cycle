package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Categories_Repository;
import ecycle.ecycle.models.Category;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Categories_Service {
        
    private final Categories_Repository categoriesRepository;

    public Category findById(String id) {
        return categoriesRepository.findById(id).orElse(null);
    }

    public List<Category> findAll () {
        return categoriesRepository.findAll();
    }

    // register a new category
    public Category save(Category category) {
        return categoriesRepository.save(category);
    }

}