package ecycle.ecycle.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import ecycle.ecycle.services.Users_Service;
import ecycle.ecycle.services.Interactions_Service;
import ecycle.ecycle.services.SingOffers_Service;
import ecycle.ecycle.services.SingRequests_Service;

@Controller @RequiredArgsConstructor
public class AppController {
    
    @Autowired Users_Service usersService;
    @Autowired Interactions_Service interactionsService;
    @Autowired SingOffers_Service singOffersService;
    @Autowired SingRequests_Service singRequestsService;

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
    }    @GetMapping("/login")
    public String login (
        @RequestParam(name="error",required=false) String error,
        @RequestParam(name="deleted",required=false) String deleted,
        Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (deleted != null) {
            model.addAttribute("success", "Your account has been successfully deleted");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login (
        @RequestParam(name="username") String username,
        @RequestParam(name="password") String password,
        HttpSession session,
        Model model
    ) {
        String hashedPassword = hashPassword(password);
        User user = usersService.findByUsernameAndPassword(username, hashedPassword);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }
        
        model.addAttribute("error", "Invalid username or password");
        return "login";
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
            return "registration";
        }
        if (usersService.findByEmail(email) != null) {
            model.addAttribute("error", "Email is already taken");
            return "registration";
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
            return "profile";
        }
        if (usersService.findByEmail(email) != null && !user.getEmail().equals(email)) {
            model.addAttribute("error", "Email is already taken");
            return "profile";
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
        return "profile";
    }

    @DeleteMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Delete the user
        usersService.delete(user);
        
        // Invalidate the session
        session.invalidate();
        
        // Redirect to login page with a parameter indicating successful deletion
        return "redirect:/login?deleted=true";
    }

}