package roshin.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexView {
    @GetMapping("/")
    public String render(Model view) {
        return "index";
    }
}
