package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Natures_Repository;
import ecycle.ecycle.models.Nature;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Natures_Service {
    
    private final Natures_Repository natures_repository;
    
    public Nature findById(String id) {
        return natures_repository.findById(id).orElse(null);
    }

    public List<Nature> findAll() {
        return natures_repository.findAll();
    }
    
    public void save(Nature nature) {
        natures_repository.save(nature);
    }

}