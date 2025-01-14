package roshin.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmokeTest {
    @RequestMapping("/ping")
    public String ping() {
        return "pong";
    }
}
