package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Characteristics_Repository;
import ecycle.ecycle.models.Characteristics;
import java.util.List;
import ecycle.ecycle.models.Model;
import ecycle.ecycle.models.Category;
import ecycle.ecycle.models.Nature;

@Service @RequiredArgsConstructor
public class Characteristics_Service {

    private final Characteristics_Repository characteristicsRepository;

    Characteristics findById(int id) {
        return characteristicsRepository.findById(id);
    }

    List<Characteristics> findAll() {
        return characteristicsRepository.findAll();
    }

    void save(Characteristics characteristics) {
        characteristicsRepository.save(characteristics);
    }

    List<Characteristics> findSimilarCharacteristics (
        String mainColour,
        String function,
        String quality,
        int prodYear,
        String batch,
        Model model,
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