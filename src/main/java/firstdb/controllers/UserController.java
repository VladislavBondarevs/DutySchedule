package firstdb.controllers;

import java.io.IOException;
import java.security.Principal;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import firstdb.model.Ticket;
import firstdb.services.DutyScheduleService;
import firstdb.services.EmailService;
import firstdb.services.TicketService;
import firstdb.services.UserService;
import firstdb.model.User;
import firstdb.services.user.UserDto;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserDetailsService userDetailsService;
    private final TicketService ticketService;
    private final UserService userService;
    @Autowired
    private DutyScheduleService dutyScheduleService;

    @Autowired
    private HttpServletRequest request;

    public UserController(UserService userService, TicketService ticketService, UserDetailsService userDetailsService, DutyScheduleService dutyScheduleService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.userDetailsService = userDetailsService;

        this.dutyScheduleService = dutyScheduleService;
    }

//    @GetMapping("/home")
//    public String home(Model model, Principal principal) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
//        model.addAttribute("userdetail", userDetails);
//        return "home";
//    }
        @GetMapping("/home")
        public String home(Model model, Principal principal) {
                 if (principal != null) {
                 UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
                 model.addAttribute("userdetail", userDetails);
                    } else {
                 return "redirect:/login";
             }
        return "home";
    }

//    @GetMapping("/home")
//    public String home(Model model, Principal principal) {
//        return Optional.ofNullable(principal)
//                .map(Principal::getName)
//                .map(userDetailsService::loadUserByUsername)
//                .map(userDetails -> {
//                    model.addAttribute("userdetail", userDetails);
//                    return "home";
//                })
//                .orElse("redirect:/login");
//    }

    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model) {
        String username = userDto.getUsername().trim();
        String fullName = userDto.getFullname().trim();

        if (username.isEmpty()) {
            model.addAttribute("usernameError", "Username cannot be empty or contain only spaces");
            return "register";
        }
        if (fullName.isEmpty()) {
            model.addAttribute("fullnameError", "Fullname cannot be empty or contain only spaces");
            return "register";
        }

        User user = userService.findByUsername(username);
        if (user != null) {
            model.addAttribute("userExistError", "User with this username already exists");
            return "register";
        }

        if ("ADMIN".equalsIgnoreCase(userDto.getRole())) {
            model.addAttribute("roleError", "You cannot assign yourself as ADMIN");
            return "register";
        }

        userService.save(userDto);

        try {
            emailService.sendEmail(userDto.getEmail(), "Welcome " + fullName,
                    "Hi, du wurdest zum Küchendienst hinzugefügt!\n\n" +
                            "Bitte sag mir, an welchem Tag du Dienst übernehmen und an welchem Tag du jemanden vertreten kannst.\n\n" +
                            "Unter Localhost: 10.10.10.16:8080 kannst du den Dienstkalender einsehen.\n\n" +
                            "Meine E-Mail-Adresse ist: Vladislav.Bondarevs@ybm-deutschland.de");

        } catch (Exception e) {
            model.addAttribute("emailError", "Error sending email: " + e.getMessage());
            return "register";
        }

        return "redirect:/register?success";
    }

    @GetMapping("/create_ticket")
    public String createTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "create_ticket";
    }

    @PostMapping("/create-ticket")
    public String createTicket(@ModelAttribute("ticket") Ticket newTicket) {
        ticketService.saveTicket(newTicket);
        return "redirect:/home";
    }

    @GetMapping("/view-reports")
    public String viewReports(Model model) {
        List<Ticket> tickets = ticketService.getAllTickets();
        model.addAttribute("tickets", tickets);
        return "view_reports";
    }
    // EMAIL SERVICE

    @GetMapping("/send-email")
    public String sendEmail(@RequestParam String email, @RequestParam String subject, @RequestParam String message, Model model) {
        try {
            emailService.sendEmail(email, subject, message);
            model.addAttribute("successMessage", "Email sent successfully to " + email);
        } catch (MessagingException e) {
            model.addAttribute("errorMessage", "Failed to send email: " + e.getMessage());
        }
        return "emailStatus";
    }


    // Registrierung mit PasswordGenerator
//    @PostMapping("/register")
//    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model) {
//        String username = userDto.getUsername().trim();
//        String fullName = userDto.getFullname().trim();
//
//        if (username.isEmpty()) {
//            model.addAttribute("usernameError", "Username cannot be empty or contain only spaces");
//            return "register";
//        }
//        if (fullName.isEmpty()) {
//            model.addAttribute("fullnameError", "Fullname cannot be empty or contain only spaces");
//            return "register";
//        }
//
//        User user = userService.findByUsername(username);
//        if (user != null) {
//            model.addAttribute("userExistError", "User with this username already exists");
//            return "register";
//        }
//
//        if ("ADMIN".equalsIgnoreCase(userDto.getRole())) {
//            model.addAttribute("roleError", "You cannot assign yourself as ADMIN");
//            return "register";
//        }
//
//        String generatedPassword = generateRandomPassword();
//
//        userDto.setPassword(generatedPassword);
//
//        userService.save(userDto);
//
//        try {
//            emailService.sendEmail(userDto.getEmail(), "Welcome " + fullName,
//                    "Hi, du wurdest zum Küchendienst hinzugefügt!\n\n" +
//                            "Dein Passwort lautet: " + generatedPassword + "\n\n" +
//                            "Bitte sag mir, an welchem Tag du Dienst übernehmen und an welchem Tag du jemanden vertreten kannst.\n\n" +
//                            "Unter Localhost: 10.10.10.16:8080 kannst du den Dienstkalender einsehen.\n\n" +
//                            "Meine E-Mail-Adresse ist: Vladislav.Bondarevs@ybm-deutschland.de");
//
//        } catch (Exception e) {
//            model.addAttribute("emailError", "Error sending email: " + e.getMessage());
//            return "register";
//        }
//        return "redirect:/register?success";
//    }
//    //Password Generator
//    private String generateRandomPassword() {
//        int length = 10;
//        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        Random random = new Random();
//        StringBuilder password = new StringBuilder();
//
//        for (int i = 0; i < length; i++) {
//            int index = random.nextInt(characterSet.length());
//            password.append(characterSet.charAt(index));
//        }
//        return password.toString();
//    }
}