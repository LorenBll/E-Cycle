package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Characteristics_Repository;
import ecycle.ecycle.models.Characteristics;
import java.util.List;
import ecycle.ecycle.models.ProductModel;
import ecycle.ecycle.models.Category;
import ecycle.ecycle.models.Nature;

@Service
@RequiredArgsConstructor
public class Characteristics_Service {

    private final Characteristics_Repository characteristicsRepository;

    public Characteristics findById(int id) {
        return characteristicsRepository.findById(id);
    }

    public List<Characteristics> findAll() {
        return characteristicsRepository.findAll();
    }

    public void save(Characteristics characteristics) {
        characteristicsRepository.save(characteristics);
    }

    public boolean isDuplicate (Characteristics characteristics) {
        List<Characteristics> characteristcsList = characteristicsRepository.findAll();
        
        // remove characteristics that do not match mainColour
        characteristcsList.removeIf(characteristics1 -> !characteristics1.getMainColour().equals(characteristics.getMainColour()));
        
        // remove characteristics that do not match function
        characteristcsList.removeIf(characteristics1 -> !characteristics1.getFunction().equals(characteristics.getFunction()));
        
        // if quality is not null, remove characteristics that do not match quality
        if (characteristics.getQuality() != null) {
            characteristcsList.removeIf(characteristics1 -> !characteristics1.getQuality().equals(characteristics.getQuality()));
        }
        
        // remove characteristics that do not match model
        characteristcsList.removeIf(characteristics1 -> !characteristics1.getModel().getId().equals(characteristics.getModel().getId()));

        // remove characteristics that do not match nature
        characteristcsList.removeIf(characteristics1 -> !characteristics1.getNature().getId().equals(characteristics.getNature().getId()));

        // remove characteristics that do not match category
        characteristcsList.removeIf(characteristics1 -> !characteristics1.getCategory().getId().equals(characteristics.getCategory().getId()));

        // if prodYear is not null, remove characteristics that do not match prodYear
        if (characteristics.getProdYear() != 0) {
            characteristcsList.removeIf(characteristics1 -> characteristics1.getProdYear() != characteristics.getProdYear());
        }

        // if batch is not null, remove characteristics that do not match batch
        if (characteristics.getBatch() != null) {
            characteristcsList.removeIf(characteristics1 -> !characteristics1.getBatch().equals(characteristics.getBatch()));
        }

        // if characteristicsList is empty, it means that there are no duplicates
        if (characteristcsList.isEmpty()) {
            return false;
        }
        else {
            return true;
        }

    }

    public List<Characteristics> findSimilarCharacteristics (
        String mainColour,
        String function,
        String quality,
        int prodYear,
        String batch,
        ProductModel model,
        Category category,
        Nature nature
    ) {
            
        List<Characteristics> characteristcsList = characteristicsRepository.findAll();

        // remove characteristics that do not match nature
        characteristcsList.removeIf(characteristics -> !characteristics.getNature().getId().equals(nature.getId()));

        // remove characteristics that do not match category
        characteristcsList.removeIf(characteristics -> !characteristics.getCategory().getId().equals(category.getId()));

        // remove characteristics that do not match model
        characteristcsList.removeIf(characteristics -> !characteristics.getModel().getId().equals(model.getId()));

        // remove characteristics that do not match mainColour
        characteristcsList.removeIf(characteristics -> !characteristics.getMainColour().equals(mainColour));

        // remove characteristics that do not match function
        characteristcsList.removeIf(characteristics -> !characteristics.getFunction().equals(function));

        // if quality is not null, remove characteristics that do not match quality
        if (quality != null) {
            characteristcsList.removeIf(characteristics -> !characteristics.getQuality().equals(quality));
        }

        // if prodYear is not null, remove characteristics that do not match prodYear
        if (prodYear != 0) {
            characteristcsList.removeIf(characteristics -> characteristics.getProdYear() != prodYear);
        }

        // if batch is not null, remove characteristics that do not match batch
        if (batch != null) {
            characteristcsList.removeIf(characteristics -> !characteristics.getBatch().equals(batch));
        }

        return characteristcsList;

    }
    
}