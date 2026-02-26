package ecycle.ecycle.models.bodies;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestRequest {
    private String category;
    private String categoryManual;
    private String nature;
    private String natureManual;
    private String brand;
    private String brandManual;
    private String model;
    private String modelManual;
    private String mainColour;
    private String function;
    private int prodYear;
    private String batch;
    private String quality;
    private int quantity;
    private float maxPricePerUnit;
}

