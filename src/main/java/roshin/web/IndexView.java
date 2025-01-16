package roshin.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring6.view.ThymeleafView;

import java.util.Collections;

@Controller
public class IndexView {
    @RequestMapping("/")
    public String index(Model view) {
        view.addAttribute("hello", "world");
        return "index";
    }
}
