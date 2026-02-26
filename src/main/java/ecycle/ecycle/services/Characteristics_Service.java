package ecycle.ecycle.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ecycle.ecycle.repositories.Characteristics_Repository;
import ecycle.ecycle.models.Characteristics;
import java.util.List;

/**
 * Service class for handling characteristics operations.
 */
@Service
@RequiredArgsConstructor
public class Characteristics_Service {

    private final Characteristics_Repository characteristicsRepository;

    /**
     * saves characteristics to the database.
     * 
     * @param characteristics the characteristics to save
     */
    public void save(Characteristics characteristics) {
        characteristicsRepository.save(characteristics);
    }

    /**
     * checks if characteristics with identical values already exist in the database.
     * 
     * @param characteristics the characteristics to check for duplicates
     * @return true if a duplicate exists, false otherwise
     */
    public boolean isDuplicate(Characteristics characteristics) {
        
        List<Characteristics> characteristcsList = characteristicsRepository.findAll();
        
        // step-by-step filtering of characteristics based on matching properties
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
        return !characteristcsList.isEmpty();
    }

    /**
     * finds and returns the duplicate characteristics if one exists.
     * 
     * @param characteristics the characteristics to find a duplicate for
     * @return the duplicate characteristics if found, null otherwise
     */
    public Characteristics findDuplicate(Characteristics characteristics) {
        
        // first check if a duplicate exists
        if (!isDuplicate(characteristics)) {
            return null;
        }

        List<Characteristics> characteristcsList = characteristicsRepository.findAll();
        
        // apply the same filtering as in isDuplicate method to find the match
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

        // return the first match, if any exists
        if (characteristcsList.isEmpty()) {
            return null; // this shouldn't be executed because we already checked for duplicates
        } else {
            return characteristcsList.get(0);
        }
    }
}
