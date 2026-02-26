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
import java.util.List;
import java.security.MessageDigest;
import java.util.Map;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Calendar;
import java.util.ArrayList;
import ecycle.ecycle.models.*;
import ecycle.ecycle.services.*;
import ecycle.ecycle.models.bodies.*;

/**
 * main controller handling all application-related requests.
 * this includes user authentication, profile management, and e-cycle's activities.
 */
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

    /**
     * creates a hashed version of user's password with a timestamp-based salt.
     * 
     * @param password The plain text password to be hashed
     * @param tsUpdate Timestamp to use as salt
     * @return Hashed password as a string
     */
    private String hashPassword ( String password , Timestamp tsUpdate ) {

        tsUpdate = new Timestamp(tsUpdate.getTime() / 1000 * 1000); // round to seconds
        String salt = tsUpdate.toString();
        String passwordWithSalt = password + salt;        
        
        String hash;
        try {
            // use sha256 hashing algorithm
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(passwordWithSalt.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            hash = sb.toString();
        } catch (Exception e) {
            // if an error occurs, return null
            e.printStackTrace();
            return null;
        }

        return hash;
    }    
    
    /**
     * performs routine system maintenance tasks:
     * 1. closes expired/outdated negotiations
     * 2. attempts to match available offers with suitable requests
     */
    private void routineCheck () {
        
        // check all negotiations and end them if necessary
        List<Negotiation> negotiations = negotiationsService.findAll();
        for (Negotiation negotiation : negotiations) {

            boolean isNegotiationOlderThan24Hours = 
                negotiation.getTsCreation().getTime() + 24 * 60 * 60 * 1000 < System.currentTimeMillis();
            boolean isRequestUserDeleted = negotiation.getSingRequest().getRequest().getUser() == null;
            boolean isOfferUserDeleted = negotiation.getSingOffer().getOffer().getUser() == null;
            boolean isOfferExpired = negotiation.getSingOffer().getExpiration().before(new Date(System.currentTimeMillis())) || 
                negotiation.getSingOffer().getExpiration().equals(new Date(System.currentTimeMillis()));
            boolean isSingRequestDelted = negotiation.getSingRequest().getTsDeletion() != null;
            boolean isSingOfferDeleted = negotiation.getSingOffer().getTsDeletion() != null;

            if (isNegotiationOlderThan24Hours || isRequestUserDeleted || isOfferUserDeleted || 
                isOfferExpired || isSingRequestDelted || isSingOfferDeleted) {
                // end the negotiation
                negotiation.setWasAccepted(false);
                negotiation.setTsClosure(new Timestamp(System.currentTimeMillis()));
                negotiationsService.save(negotiation);
            }
        }

        // get all valid singoffers
        List<SingOffer> availableSingOffers = singOffersService.findByAvailability(true);

        // for each singoffer, find a singrequest that matches the characteristics and is not already in negotiation
        for (SingOffer singOffer : availableSingOffers) {
            
            List<SingRequest> availableSingRequests = singRequestsService.findByAvailabilityAndCharacteristicsAndNotUserAndPrice(
                true, 
                singOffer.getCharacteristics(), 
                singOffer.getOffer().getUser(), 
                singOffer.getPrice()
            );

            SingRequest selectedSingRequest = null;
            for (SingRequest singRequest : availableSingRequests) {
                Negotiation negotiation = negotiationsService.findBySingOfferAndSingRequest(singOffer, selectedSingRequest);
                if (negotiation != null) {
                    continue;
                }
                selectedSingRequest = singRequest;
                break;
            }

            if (selectedSingRequest != null) {
                // create a negotiation
                Negotiation negotiation = new Negotiation();
                negotiation.setSingOffer(singOffer);
                negotiation.setSingRequest(selectedSingRequest);
                negotiation.setTsCreation(new Timestamp(System.currentTimeMillis()));
                negotiationsService.save(negotiation);
            }
        }
    }    
    
    /**
     * renders the index page.
     * 
     * @return The index view name
     */
    @GetMapping("/")
    public String index () {
        return "index";
    }

    /**
     * renders the error page.
     * 
     * @return The error view name
     */
    @GetMapping("/error")
    public String error () {
        return "error";    
    }    
    
    /**
     * renders the login page with optional error messages.
     * 
     * @param error Optional error parameter
     * @param deleted Optional delete confirmation parameter
     * @param model The Spring MVC model
     * @return The login view name
     */
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

    /**
     * processes login requests and authenticates users.
     * 
     * @param loginRequest The login request body
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to home or back to login
     */
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

        User user = usersService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return "redirect:/login?error=invalid";
        }
        String hashedPassword = this.hashPassword(loginRequest.getPassword(), user.getTsPasswordUpdate());

        if (user.getPassword().equals(hashedPassword)) {
            // login successful
            session.setAttribute("user", user);
            return "redirect:/home";
        }
        
        return "redirect:/login?error=invalid";
    }

    /**
     * logs out the current user by invalidating their session.
     * 
     * @param session The HTTP session
     * @return Redirect to login page
     */
    @GetMapping("/logout")
    public String logout (HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    /**
     * renders the registration page with optional error messages.
     * 
     * @param error Optional error parameter
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The registration view name
     */
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
    
    /**
     * processes user registration requests.
     * 
     * @param registrationRequest The registration request body
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to home or back to registration
     */
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
        
        // server-side validation for field lengths
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

        // create and save new user
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setName(registrationRequest.getName());
        user.setSurname(registrationRequest.getSurname());
        user.setEmail(registrationRequest.getEmail());
        user.setTsPasswordUpdate(new Timestamp(System.currentTimeMillis()));
        String hashedPassword = this.hashPassword(registrationRequest.getPassword(), user.getTsPasswordUpdate());
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

    /**
     * renders the user's home page with their offers and requests.
     * 
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The home view name or redirect to login
     */
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

    /**
     * renders the user's profile page with their information.
     * 
     * @param success Optional success parameter
     * @param error Optional error parameter
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The profile view name or redirect to login
     */
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
    
    /**
     * processes profile edit requests.
     * 
     * @param profileEditRequest The profile edit request body
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to profile with success or error message
     */
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
        
        // server-side validation for field lengths
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
        
        // update user information
        user.setUsername(profileEditRequest.getUsername());
        user.setName(profileEditRequest.getName());
        user.setSurname(profileEditRequest.getSurname());
        user.setEmail(profileEditRequest.getEmail());
        if (profileEditRequest.getPassword() != null) {
            user.setTsPasswordUpdate(new Timestamp(System.currentTimeMillis()));
            String password = profileEditRequest.getPassword();
            String hashedPassword = this.hashPassword(password, user.getTsPasswordUpdate());
            user.setPassword(hashedPassword);
        }
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

    /**
     * processes account deletion requests.
     * 
     * @param session The HTTP session
     * @return Redirect to login page with deletion confirmation
     */
    @DeleteMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        // check if user is logged in
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        session.invalidate();
        usersService.delete(user);
        
        // run routine checks to update negotiations
        this.routineCheck();
        
        return "redirect:/login?deleted=true";
    }

    /**
     * renders the request insertion page.
     * 
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The requestInsertion view name or redirect to login
     */
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

        // provide necessary data for the form
        model.addAttribute("user", user);
        model.addAttribute("categories", categoriesService.findAll());
        model.addAttribute("natures", naturesService.findAll());
        model.addAttribute("brands", brandsService.findAll());
        model.addAttribute("models", modelsService.findAll());
        return "requestInsertion";
    }

    /**
     * processes request insertion submissions.
     * 
     * @param requestData The request data as a map
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to home with success message
     */
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
        
        // process each characteristic from the form
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
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                prodYear = Integer.parseInt(charData.get("prodYear").toString());
                if (prodYear < 0 || prodYear > currentYear) {
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
                if (quantity < 1 || quantity > 500) {
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
                if (maxPricePerUnit < 0 || maxPricePerUnit > 500000) {
                    return "redirect:/requestInsertion?error=price_cannot_be_negative";
                }
            } catch (NumberFormatException e) {
                return "redirect:/requestInsertion?error=invalid_price_format";
            }
            charRequest.setMaxPricePerUnit(maxPricePerUnit);
            
            requestRequests.add(charRequest);
        }

        // create the interaction entity
        Interaction interaction = new Interaction();
        interaction.setTitle(title);
        interaction.setTsCreation(new Timestamp(System.currentTimeMillis()));
        interaction.setIsOffer(false);
        interaction.setUser(user);
        interactionsService.save(interaction);
        
        // create sing requests for each characteristics request
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

            // avoid duplicate characteristics
            if (!characteristicsService.isDuplicate(characteristics)) {
                characteristicsService.save(characteristics);
            } else {
                characteristics = characteristicsService.findDuplicate(characteristics);
            }

            // create sing requests for the specified quantity
            for (int i = 0; i < characteristicsRequest.getQuantity(); i++) {
                SingRequest singRequest = new SingRequest();
                singRequest.setRequest(interaction);
                singRequest.setCharacteristics(characteristics);
                singRequest.setMaxPrice(characteristicsRequest.getMaxPricePerUnit());
                singRequestsService.save(singRequest);            
            }
        }

        // run routine checks to update negotiations
        this.routineCheck();

        return "redirect:/home?requestSuccess=true";
    }

    /**
     * renders the offer insertion page.
     * 
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The offerInsertion view name or redirect to login
     */
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

        // provide necessary data for the form
        model.addAttribute("user", user);
        model.addAttribute("categories", categoriesService.findAll());
        model.addAttribute("natures", naturesService.findAll());
        model.addAttribute("brands", brandsService.findAll());
        model.addAttribute("models", modelsService.findAll());
        return "offerInsertion";
    }       
    
    /**
     * processes offer insertion submissions.
     * 
     * @param offerData The offer data as a map
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to home with success message
     */
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

        // process each characteristic from the form
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
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                prodYear = Integer.parseInt(charData.get("prodYear").toString());
                if (prodYear < 0 || prodYear > currentYear) {
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
                if (quantity < 1 || quantity > 500) {
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
                if (pricePerUnit < 0 || pricePerUnit > 500000) {
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
            Date expiration = null;
            if (expirationStr != null && !expirationStr.isEmpty()) {
                try {
                    expiration = Date.valueOf(expirationStr);
                } catch (IllegalArgumentException e) {
                    return "redirect:/offerInsertion?error=invalid_expiration_date";
                }
            }
            charOffer.setExpiration(expiration);

            // TODO: implement file upload handling for pictures
            /* 
            String filePath = (String) charData.get("filePath");
            if (filePath != null && filePath.length() > 255) {
                return "redirect:/offerInsertion?error=file_path_too_long";
            } 
            */
            charOffer.setFilePath("test");

            offerRequests.add(charOffer);
        }

        // create the interaction entity
        Interaction interaction = new Interaction();
        interaction.setTitle(title);
        interaction.setTsCreation(new Timestamp(System.currentTimeMillis()));
        interaction.setIsOffer(true);
        interaction.setUser(user);
        interactionsService.save(interaction);

        // create sing offers for each characteristics offer
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

            // avoid duplicate characteristics
            if (!characteristicsService.isDuplicate(characteristics)) {
                characteristicsService.save(characteristics);
            } else {
                characteristics = characteristicsService.findDuplicate(characteristics);
            }

            // create sing offers for the specified quantity
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

        // run routine checks to update negotiations
        this.routineCheck();

        return "redirect:/home?offerSuccess=true";
    }

    /**
     * renders the offer details page showing information about a specific offer.
     * 
     * @param offerId The ID of the offer to view
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The viewOfferDetails view name or redirect on error
     */
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
        
        // active singOffers
        // get all the singOffers associated with the offer
        List<SingOffer> activeSingOffers = singOffersService.findByOffer(offer);
        // remove all inactive singOffers from the list
        for (int i = activeSingOffers.size() - 1; i >= 0; i--) {
            SingOffer singOffer = activeSingOffers.get(i);
            if (!singOffersService.isActive(singOffer)) {
                activeSingOffers.remove(i);
            }
        }

        // determine status for each active offer
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
        
        // inactive singOffers
        // get all the inactive singOffers associated with the offer
        List<SingOffer> inactiveSingOffers = singOffersService.findByOffer(offer);
        // remove all active singOffers from the list
        for (int i = inactiveSingOffers.size() - 1; i >= 0; i--) {
            SingOffer singOffer = inactiveSingOffers.get(i);
            if (singOffersService.isActive(singOffer)) {
                inactiveSingOffers.remove(i);
            }
        }

        // determine status for each inactive offer
        List<String> inactiveStatusesList = new ArrayList<>();
        for (SingOffer singOffer : inactiveSingOffers) {
            boolean wasAccepted = (negotiationsService.findBySingOfferAndWasAccepted(singOffer, true) != null);
            boolean hasExpired = (singOffer.getExpiration() != null && singOffer.getExpiration().before(new Date(System.currentTimeMillis())));
            // add the status of the singOffer to the list
            if (wasAccepted) {
                inactiveStatusesList.add("Accepted");
            } else if (hasExpired) {
                inactiveStatusesList.add("Expired");
            } else {
                inactiveStatusesList.add("Deleted");
            }
        }
        
        // calculate total revenue from accepted offers
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

    /**
     * processes sing offer deletion requests.
     * 
     * @param singOfferId The ID of the sing offer to delete
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to home with success message
     */
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
        negotiation.setTsClosure(new Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);
        
        // run routine checks to update negotiations
        this.routineCheck();
        
        return "redirect:/home?singOfferDeleted=true";
    }

    /**
     * renders the request details page showing information about a specific request.
     * 
     * @param requestId The ID of the request to view
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return The viewRequestDetails view name or redirect on error
     */
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

        // active singRequests
        // get all the singRequests associated with the request
        List<SingRequest> activeSingRequests = singRequestsService.findByRequest(request);
        // remove all inactive singRequests from the list
        for (int i = activeSingRequests.size() - 1; i >= 0; i--) {
            SingRequest singRequest = activeSingRequests.get(i);
            if (!singRequestsService.isActive(singRequest)) {
                activeSingRequests.remove(i);
            }
        }

        // determine status for each active request
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

        // get active negotiations for each sing request
        List<Negotiation> activeNegotiations = new ArrayList<>();
        for (SingRequest singRequest : activeSingRequests) {
            Negotiation negotiation = negotiationsService.findBySingRequestAndTsClosureIsNull(singRequest);
            activeNegotiations.add(negotiation);
        }

        // inactive singRequests
        // get all the inactive singRequests associated with the request
        List<SingRequest> inactiveSingRequests = singRequestsService.findByRequest(request);
        // remove all active singRequests from the list
        for (int i = inactiveSingRequests.size() - 1; i >= 0; i--) {
            SingRequest singRequest = inactiveSingRequests.get(i);
            if (singRequestsService.isActive(singRequest)) {
                inactiveSingRequests.remove(i);
            }
        }

        // determine status for each inactive request
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

        // get inactive negotiations for each sing request
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

    /**
     * processes sing request deletion requests.
     * 
     * @param singRequestId The ID of the sing request to delete
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to view request details page
     */
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
            // return to the current request details page
            return "redirect:/viewRequestDetails?requestId=" + singRequest.getRequest().getId();
        }

        negotiation.setWasAccepted(false);
        negotiation.setTsClosure(new Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);
        
        // run routine checks to update negotiations
        this.routineCheck();
        
        // return to the current request details page
        return "redirect:/viewRequestDetails?requestId=" + singRequest.getRequest().getId();
    }

    /**
     * processes negotiation acceptance requests.
     * 
     * @param negotiationId The ID of the negotiation to accept
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to view request details page
     */
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
        negotiation.setTsClosure(new Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);

        // run routine checks to update negotiations
        this.routineCheck();
        
        // return to the current request details page
        return "redirect:/viewRequestDetails?requestId=" + negotiation.getSingRequest().getRequest().getId();
    }

    /**
     * processes negotiation rejection requests.
     * 
     * @param negotiationId The ID of the negotiation to reject
     * @param session The HTTP session
     * @param model The Spring MVC model
     * @return Redirect to view request details page
     */
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
        negotiation.setTsClosure(new Timestamp(System.currentTimeMillis()));
        negotiationsService.save(negotiation);

        // run routine checks to update negotiations
        this.routineCheck();
        
        // return to the current request details page
        return "redirect:/viewRequestDetails?requestId=" + negotiation.getSingRequest().getRequest().getId();
    }

}
