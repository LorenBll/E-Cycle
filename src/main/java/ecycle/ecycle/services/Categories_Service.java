package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Categories_Repository;
import ecycle.ecycle.models.Category;
import java.util.List;

/**
 * Service class for handling category operations.
 */
@Service
@RequiredArgsConstructor
public class Categories_Service {
        
    private final Categories_Repository categoriesRepository;

    /**
     * finds a category by its ID.
     * 
     * @param id the ID of the category
     * @return the category if found, null otherwise
     */
    public Category findById(String id) {
        return categoriesRepository.findById(id).orElse(null);
    }

    /**
     * retrieves all categories from the database.
     * 
     * @return a list of all categories
     */
    public List<Category> findAll () {
        return categoriesRepository.findAll();
    }

    /**
     * saves a category to the database.
     * 
     * @param category the category to save
     * @return the saved category with updated information
     */
    public Category save(Category category) {
        return categoriesRepository.save(category);
    }

}
