package pe.vichaval.gestionriego.security.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    @GetMapping("/login")
    String login() {
        return "auth/login";
    }
}