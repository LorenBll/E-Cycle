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
        if (session.getAttribute("user") != null) {
            session.removeAttribute("user");
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
    public String register(@RequestParam(required=false) String error , Model model, HttpSession session) {
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
        if (session.getAttribute("user") != null) {
            session.removeAttribute("user");
        }
        return "registration";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam(required=false) String name,
        @RequestParam(required=false) String surname,
        @RequestParam String username,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        @RequestParam String street,
        @RequestParam String civic,
        @RequestParam String city,
        @RequestParam String province,
        @RequestParam String region,
        @RequestParam String state,
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
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setStreet(street);
        user.setCivic(civic);
        user.setCity(city);
        user.setProvince(province);
        user.setRegion(region);
        user.setState(state);

        usersService.register(user);
        session.setAttribute("user", user);
        return "redirect:/home";

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

}
