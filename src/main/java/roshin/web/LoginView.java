package roshin.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginView {
    @GetMapping(value = "/login")
    public String render(Model view) {
        view.addAttribute("loginForm", new LoginForm());
        return "login";
    }
}
