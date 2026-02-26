package ecycle.ecycle.models.bodies;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {
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
    private Integer prodYear;
    private String batch;
    private String quality;
    private Integer quantity;
    private Float pricePerUnit;
    private String description;
    private Date expiration;
    private String filePath; // TODO
}
