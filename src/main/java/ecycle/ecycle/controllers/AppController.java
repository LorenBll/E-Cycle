package ecycle.ecycle.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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

@Controller @RequiredArgsConstructor
public class AppController {
    
    @Autowired Users_Service usersService;
    
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required=false) String error, Model model, HttpSession session) {
        if (error != null) {
            if (error.equals("wrong-credentials")) {
                model.addAttribute("error", "Wrong username or password");
            }
            else if (error.equals("error-verifying-password")) {
                model.addAttribute("error", "Error verifying password");
            }
            else {
                model.addAttribute("error", error);
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            model.addAttribute("error", "Error verifying password");
            return "redirect:login?error=error-verifying-password";
        }
        User user = usersService.findByUsernameAndPassword(username, hashedPassword);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }
        return "redirect:login?error=wrong-credentials";
    }

    @GetMapping("/registration")
    public String register(@RequestParam(required=false) String error, Model model, HttpSession session) {
        if (error != null) {
            if (error.equals("username-taken")) {
                model.addAttribute("error", "Username already taken");
            }
            else if (error.equals("email-taken")) {
                model.addAttribute("error", "Email already taken");
            }
            else if (error.equals("passwords-not-equal")) {
                model.addAttribute("error", "Passwords do not match");
            }
            else if (error.equals("error-verifying-password")) {
                model.addAttribute("error", "Error verifying password");
            }
            else {
                model.addAttribute("error", error);
            }
        }
        return "registration";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String surname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String state,
            @RequestParam String region,
            @RequestParam String province,
            @RequestParam String city,
            @RequestParam String street,
            @RequestParam String civic,
            Model model,
            HttpSession session
    ) {
        
        if (usersService.findByUsername(username) != null) {
            return "redirect:registration?error=username-taken";
        }
        if (usersService.findByEmail(email) != null) {
            return "redirect:registration?error=email-taken";
        }
        if (!password.equals(confirmPassword)) {
            return "redirect:registration?error=passwords-not-equal";
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            model.addAttribute("error", "Error verifying password");
            return "redirect:registration?error=error-verifying-password";
        }

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

        usersService.register(user);
        session.setAttribute("user", user);
        return "redirect:/home";
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error");
        
        // Get error status
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMsg = (String) request.getAttribute("jakarta.servlet.error.message");
        
        modelAndView.addObject("status", statusCode);
        modelAndView.addObject("error", errorMsg);
        
        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }    
    
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "home";
    }
    
    @GetMapping("/account")
    public String account(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "account";
    }
    
    @PostMapping("/updateAccount")
    public String updateAccount(
            @RequestParam String username,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String surname,
            @RequestParam String email,
            @RequestParam(required=false) String password,
            @RequestParam(required=false) String confirmPassword,
            @RequestParam String state,
            @RequestParam String region,
            @RequestParam String province,
            @RequestParam String city,
            @RequestParam String street,
            @RequestParam String civic,
            Model model,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Check if username is changed and already exists
        if (!username.equals(currentUser.getUsername()) && usersService.findByUsername(username) != null) {
            return "redirect:/account?error=username-taken";
        }
        // Check if email is changed and already exists
        if (!email.equals(currentUser.getEmail()) && usersService.findByEmail(email) != null) {
            return "redirect:/account?error=email-taken";
        }
        
        // Check if any changes were made to the user's information
        boolean hasChanges = false;
        
        // Check if basic information changed
        if (!username.equals(currentUser.getUsername()) ||
                (name != null && !name.equals(currentUser.getName())) ||
                (surname != null && !surname.equals(currentUser.getSurname())) ||
                !email.equals(currentUser.getEmail())) {
            hasChanges = true;
        }
        
        // Check if address information changed
        if (!state.equals(currentUser.getState()) ||
                !region.equals(currentUser.getRegion()) ||
                !province.equals(currentUser.getProvince()) ||
                !city.equals(currentUser.getCity()) ||
                !street.equals(currentUser.getStreet()) ||
                !civic.equals(currentUser.getCivic())) {
            hasChanges = true;
        }
        
        // Check if password is being changed
        if (password != null && !password.isEmpty()) {
            hasChanges = true;
            // Check password pattern
            if (!password.matches("^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.{8,}).*$")) {
                return "redirect:/account?error=invalid-password";
            }
            
            if (confirmPassword == null || !password.equals(confirmPassword)) {
                return "redirect:/account?error=passwords-not-equal";
            }
            
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                return "redirect:/account?error=error-verifying-password";
            }
            currentUser.setPassword(hashedPassword);
        }
        
        // If no changes detected, return with a no-changes message
        if (!hasChanges) {
            return "redirect:/account?nochanges=true";
        }
        
        // Update user information
        currentUser.setUsername(username);
        currentUser.setName(name);
        currentUser.setSurname(surname);
        currentUser.setEmail(email);
        
        // Update address information
        currentUser.setState(state);
        currentUser.setRegion(region);
        currentUser.setProvince(province);
        currentUser.setCity(city);
        currentUser.setStreet(street);
        currentUser.setCivic(civic);
        
        // Save updated user
        usersService.update(currentUser);
        
        // Update the session with the updated user
        session.setAttribute("user", currentUser);
        
        return "redirect:/account?success=true";
    }
}
