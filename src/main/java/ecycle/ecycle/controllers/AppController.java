package ecycle.ecycle.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import ecycle.ecycle.models.*;
import ecycle.ecycle.services.*;
import ecycle.ecycle.models.bodies.*;


@Controller
@RequiredArgsConstructor
public class AppController {

    @Autowired Users_Service usersService;
    @Autowired Interactions_Service interactionsService;
    @Autowired SingOffers_Service singOffersService; 
    @Autowired SingRequests_Service singRequestsService;
    @Autowired Brands_Service brandsService;
    @Autowired ProductModels_Service modelsService;
    @Autowired Natures_Service naturesService;
    @Autowired Categories_Service categoriesService;
    @Autowired Characteristics_Service characteristicsService;
    @Autowired Negotiations_Service negotiationsService;

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void routineCheck () {
        
        /* 
        * in the routine check, the program does the following:
        * - [done] unaccept negotiations that have been pending for more than 24 hours
        * - [done] unaccept negotiations of which singOffers or singRequests have tsDeletion set
        * - [done] checks if singOffers and singNegotiations with the same characteristics
        * - [done] checks if the user of the singOffer is not the same as the user of the singRequest
        * - [done] checks if both singOffer and singRequest do not have ts_deletion set (if is active)
        * - [done] checks if singOffer's expiration is after the current date (if is active)
        * - [done] checks if the singOffer or SingRequest have been associated negotiation that has been accepted (if is active)
        * - [done] checks if the singOffer or the singRequest is already present in pending negotiations
        * - [done] checks if the singOffer and the singRequest have been associated in a previous negotiation
        * - [done] checks if the pricePerUnit of SingOffer is less than or equal to the maxPrice of singRequest
        */
        
        List<Negotiation> negotiations = negotiationsService.findAll();
        for (Negotiation negotiation : negotiations) {
            boolean isNegotiationOlderThan24Hours = 
                negotiation.getTsClosure() == null && 
                negotiation.getTsCreation().getTime() < System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            boolean isNegotiationRelatedToDeletedSingOfferOrRequest = 
                (negotiation.getSingOffer() != null && negotiation.getSingOffer().getTsDeletion() != null) || 
                (negotiation.getSingRequest() != null && negotiation.getSingRequest().getTsDeletion() != null);
            if (isNegotiationOlderThan24Hours || isNegotiationRelatedToDeletedSingOfferOrRequest) {
                // unaccept the negotiation
                negotiation.setWasAccepted(false);
                negotiation.setTsClosure(new java.sql.Timestamp(System.currentTimeMillis()));
                negotiationsService.save(negotiation);
            }
        }

        List<SingOffer> singOffers = singOffersService.findAll();
        List<SingRequest> singRequests = singRequestsService.findAll();

        for (SingOffer singOffer : singOffers) {
            for (SingRequest singRequest : singRequests) {
                
                // check if the characteristics are the same
                if (!singOffer.getCharacteristics().equals(singRequest.getCharacteristics())) {
                    continue;
                }
                
                // check that the user of the singOffer is not the same as the user of the singRequest
                if (singOffer.getOffer().getUser().getId() == singRequest.getRequest().getUser().getId()) {
                    continue;
                }

                // check if both singOffer and singRequest do not have ts_deletion set
                if (singOffer.getTsDeletion() != null || singRequest.getTsDeletion() != null) {
                    continue;
                }

                // check if both singOffer and singRequest are active

                /* 
                check if the singOffer or the singRequest are inactive:
                - singOffer's expiration is after the current date
                - singOffer or SingRequest have been associated negotiation that has been accepted
                - singOffer or the singRequest is already present in pending negotiations
                */
                if (!singOffersService.isSingOfferActive(singOffer) || 
                    !singRequestsService.isSingRequestActive(singRequest)) {
                    continue;
                }

                // check if the singOffer are already present in pending negotiations
                Negotiation pendingNegotiationsOffer = negotiationsService.findBySingOfferAndTsClosureIsNull(singOffer);
                if (pendingNegotiationsOffer != null) {
                    continue;
                }
                Negotiation pendingNegotiationsRequest = negotiationsService.findBySingRequestAndTsClosureIsNull(singRequest);
                if (pendingNegotiationsRequest != null) {
                    continue;
                }

                // check if the singOffer and the singRequest have been associated in a previous negotiation
                List<Negotiation> previousNegotiations = negotiationsService.findBySingOfferAndSingRequest(singOffer, singRequest);
                if (!previousNegotiations.isEmpty()) {
                    continue;
                }

                // check if the pricePerUnit of SingOffer is less than or equal to the maxPrice of singRequest
                if (singOffer.getPrice() > singRequest.getMaxPrice()) {
                    continue;
                }

                // if all checks passed, create a new negotiation
                Negotiation negotiation = new Negotiation();
                negotiation.setTsCreation(new java.sql.Timestamp(System.currentTimeMillis()));
                negotiation.setSingOffer(singOffer);
                negotiation.setSingRequest(singRequest);
                negotiationsService.save(negotiation);

            }
        }

    }

    @GetMapping("/")
    public String index () {
        return "index";
    }

    @GetMapping("/error")
    public String error () {
        return "error";
    }    
    
    @GetMapping("/login")
    public String login (
        @RequestParam(name="error",required=false) String error,
        @RequestParam(name="deleted",required=false) String deleted,
        Model model
    ) {        
        // error handling
        if (error != null) {
            if (error.equals("missing")) {
                model.addAttribute("error", "Username and password are required");
            } else {
                model.addAttribute("error", "Invalid Credentials");
            }
        }
        if (deleted != null) {
            model.addAttribute("success", "Your account has been successfully deleted");
        }

        return "login";
    }

    @PostMapping("/login")
    public String login (
        @RequestBody LoginRequest loginRequest,
        HttpSession session,
        Model model
    ) {
        // check if request body or username or password is missing
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() 
            || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return "redirect:/login?error=missing";
        }
        String hashedPassword = this.hashPassword(loginRequest.getPassword());
        
        User user = usersService.findByUsernameAndPassword(loginRequest.getUsername(), hashedPassword);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }
        
        return "redirect:/login?error=invalid";
    }

    @GetMapping("/logout")
    public String logout (HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/registration")
    public String registration (
        @RequestParam(name="error",required=false) String error,
        HttpSession session,
        Model model
    ) {
        // error handling
        if (error != null) {
            if (error.equals("username")) {
                model.addAttribute("error", "Username already exists");
            } else if (error.equals("email")) {
                model.addAttribute("error", "Email already exists");
            }
        }

        return "registration";
    }    
    
    @PostMapping("/register")
    public String registration (
        @RequestBody RegistrationRequest registrationRequest,
        HttpSession session,
        Model model
    ) {
        // error handling
        if (usersService.findByUsername(registrationRequest.getUsername()) != null) {
            return "redirect:/registration?error=username";
        }
        if (usersService.findByEmail(registrationRequest.getEmail()) != null) {
            return "redirect:/registration?error=email";
        }
        
        // Server-side validation for field lengths
        if (registrationRequest.getUsername() != null && registrationRequest.getUsername().length() > 50) {
            return "redirect:/registration?error=username_too_long";
        }
        
        if (registrationRequest.getName() != null && registrationRequest.getName().length() > 50) {
            return "redirect:/registration?error=name_too_long";
        }
        
        if (registrationRequest.getSurname() != null && registrationRequest.getSurname().length() > 50) {
            return "redirect:/registration?error=surname_too_long";
        }
        
        if (registrationRequest.getEmail() != null && registrationRequest.getEmail().length() > 100) {
            return "redirect:/registration?error=email_too_long";
        }
        
        if (registrationRequest.getState() != null && registrationRequest.getState().length() > 50) {
            return "redirect:/registration?error=state_too_long";
        }
        
        if (registrationRequest.getRegion() != null && registrationRequest.getRegion().length() > 50) {
            return "redirect:/registration?error=region_too_long";
        }
        
        if (registrationRequest.getProvince() != null && registrationRequest.getProvince().length() > 50) {
            return "redirect:/registration?error=province_too_long";
        }
        
        if (registrationRequest.getCity() != null && registrationRequest.getCity().length() > 50) {
            return "redirect:/registration?error=city_too_long";
        }
        
        if (registrationRequest.getStreet() != null && registrationRequest.getStreet().length() > 100) {
            return "redirect:/registration?error=street_too_long";
        }
        
        if (registrationRequest.getCivic() != null && registrationRequest.getCivic().length() > 50) {
            return "redirect:/registration?error=civic_too_long";
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setName(registrationRequest.getName());
        user.setSurname(registrationRequest.getSurname());
        user.setEmail(registrationRequest.getEmail());
        String hashedPassword = this.hashPassword(registrationRequest.getPassword());
        user.setPassword(hashedPassword);
        user.setState(registrationRequest.getState());
        user.setRegion(registrationRequest.getRegion());
        user.setProvince(registrationRequest.getProvince());
        user.setCity(registrationRequest.getCity());
        user.setStreet(registrationRequest.getStreet());
        user.setCivic(registrationRequest.getCivic());
        usersService.save(user);

        session.setAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home (
        HttpSession session, 
        Model model
    ) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        // get the user active requests
        List<Interaction> activeRequests = interactionsService.findByUserAndIsOfferAndIsActive(user, false, true);
        // get the user inactive requests
        List<Interaction> inactiveRequests = interactionsService.findByUserAndIsOfferAndIsActive(user, false, false);
        // get the user active offers
        List<Interaction> activeOffers = interactionsService.findByUserAndIsOfferAndIsActive(user, true, true);
        // get the user inactive offers
        List<Interaction> inactiveOffers = interactionsService.findByUserAndIsOfferAndIsActive(user, true, false);
        
        model.addAttribute("activeRequests", activeRequests);
        model.addAttribute("inactiveRequests", inactiveRequests);
        model.addAttribute("activeOffers", activeOffers);
        model.addAttribute("inactiveOffers", inactiveOffers); 
        return "home";
    }

    @GetMapping("/profile")
    public String profile (
        @RequestParam(name="success",required=false) String success,
        @RequestParam(name="error",required=false) String error,
        HttpSession session, 
        Model model
    ) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // error handling
        if (success != null) {
            model.addAttribute("success", "Profile updated successfully");
        }
        if (error != null) {
            if (error.equals("username")) {
                model.addAttribute("error", "Username already exists");
            } else if (error.equals("email")) {
                model.addAttribute("error", "Email already exists");
            }
        }
        
        model.addAttribute("user", user);
        return "profile";
    }    
    
    @PutMapping("/profile/edit")
    public String editProfile (
        @RequestBody ProfileEditRequest profileEditRequest,
        HttpSession session, 
        Model model
    ) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // error handling
        if (usersService.findByUsername(profileEditRequest.getUsername()) != null && !user.getUsername().equals(profileEditRequest.getUsername())) {
            return "redirect:/profile?error=username";
        }
        if (usersService.findByEmail(profileEditRequest.getEmail()) != null && !user.getEmail().equals(profileEditRequest.getEmail())) {
            return "redirect:/profile?error=email";
        }
        
        // Server-side validation for field lengths
        if (profileEditRequest.getUsername() != null && profileEditRequest.getUsername().length() > 50) {
            return "redirect:/profile?error=username_too_long";
        }
        
        if (profileEditRequest.getName() != null && profileEditRequest.getName().length() > 50) {
            return "redirect:/profile?error=name_too_long";
        }
        
        if (profileEditRequest.getSurname() != null && profileEditRequest.getSurname().length() > 50) {
            return "redirect:/profile?error=surname_too_long";
        }
        
        if (profileEditRequest.getEmail() != null && profileEditRequest.getEmail().length() > 100) {
            return "redirect:/profile?error=email_too_long";
        }
        
        if (profileEditRequest.getState() != null && profileEditRequest.getState().length() > 50) {
            return "redirect:/profile?error=state_too_long";
        }
        
        if (profileEditRequest.getRegion() != null && profileEditRequest.getRegion().length() > 50) {
            return "redirect:/profile?error=region_too_long";
        }
        
        if (profileEditRequest.getProvince() != null && profileEditRequest.getProvince().length() > 50) {
            return "redirect:/profile?error=province_too_long";
        }
        
        if (profileEditRequest.getCity() != null && profileEditRequest.getCity().length() > 50) {
            return "redirect:/profile?error=city_too_long";
        }
        
        if (profileEditRequest.getStreet() != null && profileEditRequest.getStreet().length() > 100) {
            return "redirect:/profile?error=street_too_long";
        }
        
        if (profileEditRequest.getCivic() != null && profileEditRequest.getCivic().length() > 50) {
            return "redirect:/profile?error=civic_too_long";
        }
        
        if (profileEditRequest.getPassword() != null && !profileEditRequest.getPassword().isEmpty()) {
            String hashedPassword = hashPassword(profileEditRequest.getPassword());
            user.setPassword(hashedPassword);
        }

        user.setUsername(profileEditRequest.getUsername());
        user.setName(profileEditRequest.getName());
        user.setSurname(profileEditRequest.getSurname());
        user.setEmail(profileEditRequest.getEmail());
        user.setState(profileEditRequest.getState());
        user.setRegion(profileEditRequest.getRegion());
        user.setProvince(profileEditRequest.getProvince());
        user.setCity(profileEditRequest.getCity());
        user.setStreet(profileEditRequest.getStreet());
        user.setCivic(profileEditRequest.getCivic());
        usersService.save(user);

        model.addAttribute("user", user);
        return "redirect:/profile?success=true";
    }

    @DeleteMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        usersService.delete(user);        
        session.invalidate();
        
        return "redirect:/login?deleted=true";
    }

    @GetMapping("/requestInsertion")
    public String requestInsertion (
        HttpSession session, 
        Model model
    ) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("categories", categoriesService.findAll());
        model.addAttribute("natures", naturesService.findAll());
        model.addAttribute("brands", brandsService.findAll());
        model.addAttribute("models", modelsService.findAll());
        return "requestInsertion";
    }

    @PostMapping("/insertRequest")
    public String insertRequest (
        @RequestBody Map<String, Object> requestData,
        HttpSession session, 
        Model model
    ) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String title = (String) requestData.get("title");
        
        // server-side validation for title length
        if (title != null && title.length() > 50) {
            return "redirect:/requestInsertion?error=title_too_long";
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> characteristicsData = (List<Map<String, Object>>) requestData.get("characteristics");
        List<RequestRequest> requestRequests = new ArrayList<>();
        
        for (Map<String, Object> charData : characteristicsData) {
            
            RequestRequest charRequest = new RequestRequest();
            
            // process category
            String category = (String) charData.get("category");
            String categoryManual = (String) charData.get("categoryManual");
            if (categoryManual != null && categoryManual.length() > 50) {
                return "redirect:/requestInsertion?error=category_too_long";
            }
            charRequest.setCategory(category);
            charRequest.setCategoryManual(categoryManual);
            
            // process nature
            String nature = (String) charData.get("nature");
            String natureManual = (String) charData.get("natureManual");
            if (natureManual != null && natureManual.length() > 50) {
                return "redirect:/requestInsertion?error=nature_too_long";
            }
            charRequest.setNature(nature);
            charRequest.setNatureManual(natureManual);
            
            // process brand
            String brand = (String) charData.get("brand");
            String brandManual = (String) charData.get("brandManual");
            if (brandManual != null && brandManual.length() > 50) {
                return "redirect:/requestInsertion?error=brand_too_long";
            }
            charRequest.setBrand(brand);
            charRequest.setBrandManual(brandManual);
            
            // process model
            String modelValue = (String) charData.get("model");
            String modelManual = (String) charData.get("modelManual");
            if (modelManual != null && modelManual.length() > 50) {
                return "redirect:/requestInsertion?error=model_too_long";
            }
            charRequest.setModel(modelValue);
            charRequest.setModelManual(modelManual);
            
            // process mainColour            
            String mainColour = (String) charData.get("mainColour");
            if (mainColour != null && mainColour.length() > 50) {
                return "redirect:/requestInsertion?error=mainColour_too_long";
            }
            charRequest.setMainColour(mainColour);
            
            // process function
            String function = (String) charData.get("function");
            if (function != null && function.length() > 50) {
                return "redirect:/requestInsertion?error=function_too_long";
            }
            charRequest.setFunction(function);
            
            // process batch
            String batch = (String) charData.get("batch");
            if (batch != null && batch.length() > 50) {
                return "redirect:/requestInsertion?error=batch_too_long";
            }
            charRequest.setBatch(batch);
            
            // process year
            int prodYear;
            try {
                prodYear = Integer.parseInt(charData.get("prodYear").toString());
                if (prodYear < 0) {
                    return "redirect:/requestInsertion?error=invalid_year";
                }
            } catch (NumberFormatException e) {
                return "redirect:/requestInsertion?error=invalid_year_format";
            }
            charRequest.setProdYear(prodYear);
            
            // process quality
            String quality = (String) charData.get("quality");
            if (quality == null || 
                (!quality.equals("Excellent") && 
                 !quality.equals("Good") && 
                 !quality.equals("Acceptable") && 
                 !quality.equals("Needs Revision") && 
                 !quality.equals("Broken"))) {
                return "redirect:/requestInsertion?error=invalid_quality";
            }
            charRequest.setQuality(quality);
            
            // process quantity
            int quantity;
            try {
                quantity = Integer.parseInt(charData.get("quantity").toString());
                if (quantity < 1) {
                    return "redirect:/requestInsertion?error=quantity_must_be_at_least_1";
                }
            } catch (NumberFormatException e) {
                return "redirect:/requestInsertion?error=invalid_quantity_format";
            }
            charRequest.setQuantity(quantity);
            
            // process price
            float maxPricePerUnit;
            try {
                maxPricePerUnit = Float.parseFloat(charData.get("maxPricePerUnit").toString());
                if (maxPricePerUnit < 0) {
                    return "redirect:/requestInsertion?error=price_cannot_be_negative";
                }
            } catch (NumberFormatException e) {
                return "redirect:/requestInsertion?error=invalid_price_format";
            }
            charRequest.setMaxPricePerUnit(maxPricePerUnit);
            
            requestRequests.add(charRequest);
        
        }

        Interaction interaction = new Interaction();
        interaction.setTitle(title);
        interaction.setTsCreation(new java.sql.Timestamp(System.currentTimeMillis()));
        interaction.setIsOffer(false);
        interaction.setUser(user);
        interactionsService.save(interaction);
        
        for (RequestRequest characteristicsRequest : requestRequests) {
    
            Characteristics characteristics = new Characteristics();

            // check if there is already an omonymous category
            Category category = categoriesService.findById(characteristicsRequest.getCategory());
            if (category == null) {
                category = new Category();
                category.setId(characteristicsRequest.getCategory());
                categoriesService.save(category);
            }
            characteristics.setCategory(category);

            // check if there is already an omonymous nature
            Nature nature = naturesService.findById(characteristicsRequest.getNature());
            if (nature == null) {
                nature = new Nature();
                nature.setId(characteristicsRequest.getNature());
                naturesService.save(nature);
            }
            characteristics.setNature(nature);

            // check if there is already an omonymous brand
            Brand brand = brandsService.findById(characteristicsRequest.getBrand());
            if (brand == null) {
                brand = new Brand();
                brand.setId(characteristicsRequest.getBrand());
                brandsService.save(brand);
            }
            // check if there is already an omonymous model
            ProductModel productModel = modelsService.findById(characteristicsRequest.getModel());
            if (productModel == null) {
                productModel = new ProductModel();
                productModel.setId(characteristicsRequest.getModel());
                productModel.setBrand(brand);
                modelsService.save(productModel);
            }
            characteristics.setModel(productModel);

            characteristics.setMainColour(characteristicsRequest.getMainColour());
            characteristics.setFunction(characteristicsRequest.getFunction());
            characteristics.setQuality(characteristicsRequest.getQuality());
            characteristics.setProdYear(characteristicsRequest.getProdYear());
            characteristics.setBatch(characteristicsRequest.getBatch());

            if (!characteristicsService.isDuplicate(characteristics)) {
                characteristicsService.save(characteristics);
            } else {
                characteristics = characteristicsService.findDuplicate(characteristics);
            }

            for (int i = 0; i < characteristicsRequest.getQuantity(); i++) {
                SingRequest singRequest = new SingRequest();
                singRequest.setRequest(interaction);
                singRequest.setCharacteristics(characteristics);
                singRequest.setMaxPrice(characteristicsRequest.getMaxPricePerUnit());
                singRequestsService.save(singRequest);            
            }

        }

        // look for possible negotiations
        this.routineCheck();

        return "redirect:/home?requestSuccess=true";
    }

    @GetMapping("/offerInsertion")
    public String offerInsertion (
        HttpSession session, 
        Model model
    ) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("categories", categoriesService.findAll());
        model.addAttribute("natures", naturesService.findAll());
        model.addAttribute("brands", brandsService.findAll());
        model.addAttribute("models", modelsService.findAll());
        return "offerInsertion";
    }       
    
    @PostMapping("/insertOffer")
    public String insertOffer (
        @RequestBody Map<String, Object> offerData,
        HttpSession session, 
        Model model
    ) {
        // check if the user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String title = (String) offerData.get("title");

        // server-side validation for title length
        if (title != null && title.length() > 50) {
            return "redirect:/offerInsertion?error=title_too_long";
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> characteristicsData = (List<Map<String, Object>>) offerData.get("characteristics");
        List<OfferRequest> offerRequests = new ArrayList<>();

        for (Map<String, Object> charData : characteristicsData) {
            
            OfferRequest charOffer = new OfferRequest();
            
            // process category
            String category = (String) charData.get("category");
            String categoryManual = (String) charData.get("categoryManual");
            if (categoryManual != null && categoryManual.length() > 50) {
                return "redirect:/offerInsertion?error=category_too_long";
            }
            charOffer.setCategory(category);
            charOffer.setCategoryManual(categoryManual);
            
            // process nature
            String nature = (String) charData.get("nature");
            String natureManual = (String) charData.get("natureManual");
            if (natureManual != null && natureManual.length() > 50) {
                return "redirect:/offerInsertion?error=nature_too_long";
            }
            charOffer.setNature(nature);
            charOffer.setNatureManual(natureManual);
            
            // process brand
            String brand = (String) charData.get("brand");
            String brandManual = (String) charData.get("brandManual");
            if (brandManual != null && brandManual.length() > 50) {
                return "redirect:/offerInsertion?error=brand_too_long";
            }
            charOffer.setBrand(brand);
            charOffer.setBrandManual(brandManual);
            
            // process model
            String modelValue = (String) charData.get("model");
            String modelManual = (String) charData.get("modelManual");
            if (modelManual != null && modelManual.length() > 50) {
                return "redirect:/offerInsertion?error=model_too_long";
            }
            charOffer.setModel(modelValue);
            charOffer.setModelManual(modelManual);
            
            // process mainColour            
            String mainColour = (String) charData.get("mainColour");
            if (mainColour != null && mainColour.length() > 50) {
                return "redirect:/offerInsertion?error=mainColour_too_long";
            }
            charOffer.setMainColour(mainColour);
            
            // process function
            String function = (String) charData.get("function");
            if (function != null && function.length() > 50) {
                return "redirect:/offerInsertion?error=function_too_long";
            }
            charOffer.setFunction(function);
            
            // process batch
            String batch = (String) charData.get("batch");
            if (batch != null && batch.length() > 50) {
                return "redirect:/offerInsertion?error=batch_too_long";
            }
            charOffer.setBatch(batch);

            // process year
            int prodYear;
            try {
                prodYear = Integer.parseInt(charData.get("prodYear").toString());
                if (prodYear < 0) {
                    return "redirect:/offerInsertion?error=invalid_year";
                }
            } catch (NumberFormatException e) {
                return "redirect:/offerInsertion?error=invalid_year_format";
            }
            charOffer.setProdYear(prodYear);

            // process quality
            String quality = (String) charData.get("quality");
            if (quality == null || 
                (!quality.equals("Excellent") && 
                 !quality.equals("Good") && 
                 !quality.equals("Acceptable") && 
                 !quality.equals("Needs Revision") && 
                 !quality.equals("Broken"))) {
                return "redirect:/offerInsertion?error=invalid_quality";
            }
            charOffer.setQuality(quality);

            // process quantity
            int quantity;
            try {
                quantity = Integer.parseInt(charData.get("quantity").toString());
                if (quantity < 1) {
                    return "redirect:/offerInsertion?error=quantity_must_be_at_least_1";
                }
            } catch (NumberFormatException e) {
                return "redirect:/offerInsertion?error=invalid_quantity_format";
            }
            charOffer.setQuantity(quantity);

            // process price
            float pricePerUnit;
            try {
                pricePerUnit = Float.parseFloat(charData.get("pricePerUnit").toString());
                if (pricePerUnit < 0) {
                    return "redirect:/offerInsertion?error=price_cannot_be_negative";
                }
            } catch (NumberFormatException e) {
                return "redirect:/offerInsertion?error=invalid_price_format";
            }
            charOffer.setPricePerUnit(pricePerUnit);

            // process description
            String description = (String) charData.get("description");
            if (description != null && description.length() > 255) {
                return "redirect:/offerInsertion?error=description_too_long";
            }
            charOffer.setDescription(description);
            
            // process expiration
            String expirationStr = (String) charData.get("expirationDate");
            java.sql.Date expiration = null;
            if (expirationStr != null && !expirationStr.isEmpty()) {
                try {
                    expiration = java.sql.Date.valueOf(expirationStr);
                } catch (IllegalArgumentException e) {
                    return "redirect:/offerInsertion?error=invalid_expiration_date";
                }
            }
            charOffer.setExpiration(expiration);

            // tocheck process file path of the picture
            /* 
            String filePath = (String) charData.get("filePath");
            if (filePath != null && filePath.length() > 255) {
                return "redirect:/offerInsertion?error=file_path_too_long";
            } 
            */
            charOffer.setFilePath("test");

            offerRequests.add(charOffer);

        }

        Interaction interaction = new Interaction();
        interaction.setTitle(title);
        interaction.setTsCreation(new java.sql.Timestamp(System.currentTimeMillis()));
        interaction.setIsOffer(true);
        interaction.setUser(user);
        interactionsService.save(interaction);

        for (OfferRequest characteristicsRequest : offerRequests) {
    
            Characteristics characteristics = new Characteristics();

            // check if there is already an omonymous category
            Category category = categoriesService.findById(characteristicsRequest.getCategory());
            if (category == null) {
                category = new Category();
                category.setId(characteristicsRequest.getCategory());
                categoriesService.save(category);
            }
            characteristics.setCategory(category);

            // check if there is already an omonymous nature
            Nature nature = naturesService.findById(characteristicsRequest.getNature());
            if (nature == null) {
                nature = new Nature();
                nature.setId(characteristicsRequest.getNature());
                naturesService.save(nature);
            }
            characteristics.setNature(nature);

            // check if there is already an omonymous brand
            Brand brand = brandsService.findById(characteristicsRequest.getBrand());
            if (brand == null) {
                brand = new Brand();
                brand.setId(characteristicsRequest.getBrand());
                brandsService.save(brand);
            }
            // check if there is already an omonymous model
            ProductModel productModel = modelsService.findById(characteristicsRequest.getModel());
            if (productModel == null) {
                productModel = new ProductModel();
                productModel.setId(characteristicsRequest.getModel());
                productModel.setBrand(brand);
                modelsService.save(productModel);
            }
            characteristics.setModel(productModel);

            characteristics.setMainColour(characteristicsRequest.getMainColour());
            characteristics.setFunction(characteristicsRequest.getFunction());
            characteristics.setQuality(characteristicsRequest.getQuality());
            characteristics.setProdYear(characteristicsRequest.getProdYear());
            characteristics.setBatch(characteristicsRequest.getBatch());

            if (!characteristicsService.isDuplicate(characteristics)) {
                characteristicsService.save(characteristics);
            } else {
                characteristics = characteristicsService.findDuplicate(characteristics);
            }

            for (int i = 0; i < characteristicsRequest.getQuantity(); i++) {
                SingOffer singOffer = new SingOffer();
                singOffer.setOffer(interaction);
                singOffer.setCharacteristics(characteristics);
                singOffer.setPrice(characteristicsRequest.getPricePerUnit());
                singOffer.setDescription(characteristicsRequest.getDescription());
                singOffer.setExpiration(characteristicsRequest.getExpiration());
                singOffer.setPicturePath(characteristicsRequest.getFilePath());
                singOffersService.save(singOffer);
            }

        }

        // look for possible negotiations
        this.routineCheck();

        return "redirect:/home?offerSuccess=true";
    }

    @GetMapping("/viewOfferDetails")
    public String viewOfferDetails (
        @RequestParam(name="offerId") int offerId,
        HttpSession session, 
        Model model
    ) {
        // get the offer (of class interaction) by id
        Interaction offer = interactionsService.findById(offerId);
        if (offer == null || !offer.getIsOffer()) {
            return "redirect:/home?error=offer_not_found";
        }
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // check that the logged in user is the owner of the offer
        if (!(offer.getUser().getId() == user.getId())) {
            return "redirect:/home?error=not_authorized";
        }        
        
        //. active singOffers
        // get all the singOffers associated with the offer
        List<SingOffer> activeSingOffers = singOffersService.findByOffer(offer);
        // remove all inactive singOffers from the list
        for (int i = activeSingOffers.size() - 1; i >= 0; i--) {
            SingOffer singOffer = activeSingOffers.get(i);
            if (!singOffersService.isSingOfferActive(singOffer)) {
                activeSingOffers.remove(i);
            }
        }

        List<String> activeStatusesList = new ArrayList<>();
        for (SingOffer singOffer : activeSingOffers) {
            boolean pendingNegotiation = (negotiationsService.findBySingOfferAndTsClosureIsNull(singOffer) != null);                        
            // add the status of the singOffer to the list
            if (pendingNegotiation) {
                activeStatusesList.add("Pending Negotiation");
            } else {
                activeStatusesList.add("Waiting");
            }
        }        
        
        //. inactive singOffers
        // get all the inactive singOffers associated with the offer
        List<SingOffer> inactiveSingOffers = singOffersService.findByOffer(offer);
        // remove all active singOffers from the list
        for (int i = inactiveSingOffers.size() - 1; i >= 0; i--) {
            SingOffer singOffer = inactiveSingOffers.get(i);
            if (singOffersService.isSingOfferActive(singOffer)) {
                inactiveSingOffers.remove(i);
            }
        }

        List<String> inactiveStatusesList = new ArrayList<>();
        for (SingOffer singOffer : inactiveSingOffers) {
            boolean wasAccepted = (negotiationsService.findBySingOfferAndWasAccepted(singOffer, true) != null);
            boolean hasExpired = (singOffer.getExpiration() != null && singOffer.getExpiration().before(new java.sql.Date(System.currentTimeMillis())));
            // add the status of the singOffer to the list
            if (wasAccepted) {
                inactiveStatusesList.add("Accepted");
            } else if (hasExpired) {
                inactiveStatusesList.add("Expired");
            } else {
                inactiveStatusesList.add("Deleted");
            }
        }
        
        float revenue = 0;
        for (SingOffer singOffer : inactiveSingOffers ) {
            if (negotiationsService.findBySingOfferAndWasAccepted(singOffer, true) != null) {
                revenue += singOffer.getPrice();
            }
        }
        
        model.addAttribute("user", user);
        model.addAttribute("offer", offer);
        model.addAttribute("singOffers", activeSingOffers);
        model.addAttribute("statuses", activeStatusesList);
        model.addAttribute("inactiveSingOffers", inactiveSingOffers);
        model.addAttribute("inactiveStatuses", inactiveStatusesList);
        model.addAttribute("revenue", revenue);
        return "viewOfferDetails";
    }

    @PutMapping("/deleteSingOffer")
    public String deleteSingOffer (
        @RequestParam(name="singOfferId") int singOfferId,
        HttpSession session, 
        Model model
    ) {
        // check if the singOffer exists
        SingOffer singOffer = singOffersService.findById(singOfferId);
        if (singOffer == null) {
            return "redirect:/home?error=singOffer_not_found";
        }
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // check if the logged in user is the owner of the singOffer
        if (!(singOffer.getOffer().getUser().getId() == user.getId())) {
            return "redirect:/home?error=not_authorized";
        }
    
        // delete the singOffer
        singOffersService.delete(singOffer);

        // reject the negotiation associated with the singOffer
        Negotiation negotiation = negotiationsService.findBySingOfferAndTsClosureIsNull(singOffer);
        if (negotiation == null) {
            return "redirect:/home?offerDeleted=true";
        }
        negotiation.setWasAccepted(false);
        negotiation.setTsClosure(new java.sql.Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);
        this.routineCheck();
        return "redirect:/home?singOfferDeleted=true";
    }

    @GetMapping("/viewRequestDetails")
    public String viewRequestDetails (
        @RequestParam(name="requestId") int requestId,
        HttpSession session, 
        Model model
    ) {
        // get the request (of class interaction) by id
        Interaction request = interactionsService.findById(requestId);
        if (request == null || request.getIsOffer()) {
            return "redirect:/home?error=request_not_found";
        }
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // check that the logged in user is the owner of the request
        if (!(request.getUser().getId() == user.getId())) {
            return "redirect:/home?error=not_authorized";
        }

        //. active singRequests
        // get all the singRequests associated with the request
        List<SingRequest> activeSingRequests = singRequestsService.findByRequest(request);
        // remove all inactive singRequests from the list
        for (int i = activeSingRequests.size() - 1; i >= 0; i--) {
            SingRequest singRequest = activeSingRequests.get(i);
            if (!singRequestsService.isSingRequestActive(singRequest)) {
                activeSingRequests.remove(i);
            }
        }

        List<String> activeStatusesList = new ArrayList<>();
        for (SingRequest singRequest : activeSingRequests) {
            boolean pendingNegotiation = (negotiationsService.findBySingRequestAndTsClosureIsNull(singRequest) != null);                        
            // add the status of the singRequest to the list
            if (pendingNegotiation) {
                activeStatusesList.add("Pending Negotiation");
            } else {
                activeStatusesList.add("Waiting");
            }
        }

        List<Negotiation> activeNegotiations = new ArrayList<>();
        for (SingRequest singRequest : activeSingRequests) {
            Negotiation negotiation = negotiationsService.findBySingRequestAndTsClosureIsNull(singRequest);
            activeNegotiations.add(negotiation);
        }

        //. inactive singRequests
        // get all the inactive singRequests associated with the request
        List<SingRequest> inactiveSingRequests = singRequestsService.findByRequest(request);
        // remove all active singRequests from the list
        for (int i = inactiveSingRequests.size() - 1; i >= 0; i--) {
            SingRequest singRequest = inactiveSingRequests.get(i);
            if (singRequestsService.isSingRequestActive(singRequest)) {
                inactiveSingRequests.remove(i);
            }
        }

        List<String> inactiveStatusesList = new ArrayList<>();
        for (SingRequest singRequest : inactiveSingRequests) {
            boolean wasAccepted = (negotiationsService.findBySingRequestAndWasAccepted(singRequest, true) != null);
            // add the status of the singRequest to the list
            if (wasAccepted) {
                inactiveStatusesList.add("Accepted");
            } else {
                inactiveStatusesList.add("Deleted");
            }
        }

        List<Negotiation> inactiveNegotiations = new ArrayList<>();
        for (SingRequest singRequest : inactiveSingRequests) {
            Negotiation negotiation = negotiationsService.findBySingRequestAndWasAccepted(singRequest, true);
            inactiveNegotiations.add(negotiation);
        }

        model.addAttribute("user", user);
        model.addAttribute("request", request);
        model.addAttribute("singRequests", activeSingRequests);
        model.addAttribute("statuses", activeStatusesList);
        model.addAttribute("activeNegotiations", activeNegotiations);
        model.addAttribute("inactiveSingRequests", inactiveSingRequests);
        model.addAttribute("inactiveStatuses", inactiveStatusesList);
        model.addAttribute("inactiveNegotiations", inactiveNegotiations);
        return "viewRequestDetails";
    }

    @PutMapping("/deleteSingRequest")
    public String deleteSingRequest (
        @RequestParam(name="singRequestId") int singRequestId,
        HttpSession session, 
        Model model
    ) {
        // check if the singRequest exists
        SingRequest singRequest = singRequestsService.findById(singRequestId);
        if (singRequest == null) {
            return "redirect:/home?error=singRequest_not_found";
        }
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // check if the logged in user is the owner of the singRequest
        if (!(singRequest.getRequest().getUser().getId() == user.getId())) {
            return "redirect:/home?error=not_authorized";
        }

        // delete the singRequest
        singRequestsService.delete(singRequest);

        // reject the negotiation associated with the singRequest
        Negotiation negotiation = negotiationsService.findBySingRequestAndTsClosureIsNull(singRequest);
        if (negotiation == null) {
            return "redirect:/home?singRequestDeleted=true";
        }
        negotiation.setWasAccepted(false);
        negotiation.setTsClosure(new java.sql.Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);
        this.routineCheck();
        return "redirect:/home?singRequestDeleted=true";
    }

    @PutMapping("/acceptNegotiation")
    public String acceptNegotiation (
        @RequestParam(name="negotiationId") int negotiationId,
        HttpSession session, 
        Model model
    ) {
        // check if the negotiation exists
        Negotiation negotiation = negotiationsService.findById(negotiationId);
        if (negotiation == null) {
            return "redirect:/home?error=negotiation_not_found";
        }
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // check if the logged in user is the owner of the negotiation
        if (!(negotiation.getSingRequest().getRequest().getUser().getId() == user.getId())) {
            return "redirect:/home?error=not_authorized";
        }

        // accept the negotiation
        negotiation.setWasAccepted(true);
        negotiation.setTsClosure(new java.sql.Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);

        this.routineCheck();
        
        return "redirect:/home?negotiationAccepted=true";
    }

    @PutMapping("/rejectNegotiation")
    public String rejectNegotiation (
        @RequestParam(name="negotiationId") int negotiationId,
        HttpSession session, 
        Model model
    ) {
        // check if the negotiation exists
        Negotiation negotiation = negotiationsService.findById(negotiationId);
        if (negotiation == null) {
            return "redirect:/home?error=negotiation_not_found";
        }
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // check if the logged in user is the owner of the negotiation
        if (!(negotiation.getSingRequest().getRequest().getUser().getId() == user.getId())) {
            return "redirect:/home?error=not_authorized";
        }

        // reject the negotiation
        negotiation.setWasAccepted(false);
        negotiation.setTsClosure(new java.sql.Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);

        this.routineCheck();
        
        return "redirect:/home?negotiationRejected=true";
    }

}