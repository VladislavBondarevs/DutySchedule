package firstdb.controllers;

import firstdb.model.User;
import firstdb.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {


    private final UserServiceImpl userServiceImpl;
    @Autowired
    public DashboardController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userServiceImpl.getAllUsers();
        model.addAttribute("users", users);
        return "dashboard";
    }
}
