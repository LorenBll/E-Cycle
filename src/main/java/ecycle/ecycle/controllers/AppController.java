package ecycle.ecycle.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import ecycle.ecycle.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import ecycle.ecycle.models.*;
import ecycle.ecycle.services.*;


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

    String hashPassword(String password) {
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
        @RequestParam(name="username", required=false) String username,
        @RequestParam(name="password", required=false) String password,
        HttpSession session,
        Model model
    ) {
        // Check if username or password is missing
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/login?error=missing";
        }
        
        String hashedPassword = hashPassword(password);
        User user = usersService.findByUsernameAndPassword(username, hashedPassword);
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
    public String registration () {
        return "registration";
    }

    @PostMapping("/register")
    public String registration (
        @RequestParam(name="username") String username,
        @RequestParam(name="name",required=false) String name,
        @RequestParam(name="surname",required=false) String surname,
        @RequestParam(name="email") String email,
        @RequestParam(name="password") String password,
        @RequestParam(name="state") String state,
        @RequestParam(name="region") String region,
        @RequestParam(name="province") String province,
        @RequestParam(name="city") String city,
        @RequestParam(name="street") String street,
        @RequestParam(name="civic") String civic,
        HttpSession session,
        Model model
    ) {
        if (usersService.findByUsername(username) != null) {
            model.addAttribute("error", "Username is already taken");
            return "redirect:/registration";
        }
        if (usersService.findByEmail(email) != null) {
            model.addAttribute("error", "Email is already taken");
            return "redirect:/registration";
        }

        String hashedPassword = hashPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setState(state);
        user.setRegion(region);
        user.setProvince(province);
        user.setCity(city);
        user.setStreet(street);
        user.setCivic(civic);
        usersService.save(user);

        session.setAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home (
        HttpSession session, 
        Model model
    ) {
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
        HttpSession session, 
        Model model
    ) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile (
        @RequestParam(name="username") String username,
        @RequestParam(name="name",required=false) String name,
        @RequestParam(name="surname",required=false) String surname,
        @RequestParam(name="email") String email,
        @RequestParam(name="password",required=false) String password,
        @RequestParam(name="state") String state,
        @RequestParam(name="region") String region,
        @RequestParam(name="province") String province,
        @RequestParam(name="city") String city,
        @RequestParam(name="street") String street,
        @RequestParam(name="civic") String civic,
        HttpSession session, 
        Model model
    ) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (usersService.findByUsername(username) != null && !user.getUsername().equals(username)) {
            model.addAttribute("error", "Username is already taken");
            return "redirect:/profile";
        }
        if (usersService.findByEmail(email) != null && !user.getEmail().equals(email)) {
            model.addAttribute("error", "Email is already taken");
            return "redirect:/profile";
        }
        if (password != null && !password.isEmpty()) {
            String hashedPassword = hashPassword(password);
            user.setPassword(hashedPassword);
        }
        
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setState(state);
        user.setRegion(region);
        user.setProvince(province);
        user.setCity(city);        user.setStreet(street);
        user.setCivic(civic);
        
        usersService.save(user);

        model.addAttribute("user", user);
        model.addAttribute("success", "Profile updated successfully");
        return "redirect:/profile";
    }

    @DeleteMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        session.invalidate();
        usersService.delete(user);        
        
        // Redirect to login page with a parameter indicating successful deletion
        return "redirect:/login?deleted=true";
    }

    @GetMapping("/requestInsertion")
    public String requestInsertion (
        HttpSession session, 
        Model model
    ) {
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
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        String title = (String) requestData.get("title");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> characteristicsData = (List<Map<String, Object>>) requestData.get("characteristics");
        List<CharacteristicsRequest> characteristicsRequests = new ArrayList<>();
        
        for (Map<String, Object> charData : characteristicsData) {
            CharacteristicsRequest charRequest = new CharacteristicsRequest();
            
            // Process category
            String category = (String) charData.get("category");
            String categoryManual = (String) charData.get("categoryManual");
            charRequest.setCategory(category);
            charRequest.setCategoryManual(categoryManual);
            
            // Process nature
            String nature = (String) charData.get("nature");
            String natureManual = (String) charData.get("natureManual");
            charRequest.setNature(nature);
            charRequest.setNatureManual(natureManual);
            
            // Process brand
            String brand = (String) charData.get("brand");
            String brandManual = (String) charData.get("brandManual");
            charRequest.setBrand(brand);
            charRequest.setBrandManual(brandManual);
            
            // Process model
            String modelValue = (String) charData.get("model");
            String modelManual = (String) charData.get("modelManual");
            charRequest.setModel(modelValue);
            charRequest.setModelManual(modelManual);
            
            // Process other fields
            charRequest.setMainColour((String) charData.get("mainColour"));
            charRequest.setFunction((String) charData.get("function"));
            charRequest.setProdYear(Integer.parseInt(charData.get("prodYear").toString()));
            charRequest.setBatch((String) charData.get("batch"));
            charRequest.setQuality((String) charData.get("quality"));
            charRequest.setQuantity(Integer.parseInt(charData.get("quantity").toString()));
            charRequest.setMaxPricePerUnit(Float.parseFloat(charData.get("maxPricePerUnit").toString()));
            
            characteristicsRequests.add(charRequest);
        }

        Interaction interaction = new Interaction();
        interaction.setTitle(title);
        interaction.setTsCreation(new java.sql.Timestamp(System.currentTimeMillis()));
        interaction.setIsOffer(false);
        interaction.setUser(user);
        interactionsService.save(interaction);
        for (CharacteristicsRequest characteristicsRequest : characteristicsRequests) {
    
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
            }
            
            for (int i = 0; i < characteristicsRequest.getQuantity(); i++) {
                SingRequest singRequest = new SingRequest();
                singRequest.setRequest(interaction);
                singRequest.setCharacteristics(characteristics);
                singRequest.setMaxPrice(characteristicsRequest.getMaxPricePerUnit());
                singRequestsService.save(singRequest);
            }

        }

        // todo research for possible negotiations
        return "redirect:/home?requestSuccess=true";
    }

}