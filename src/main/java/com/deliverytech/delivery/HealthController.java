package com.deliverytech.delivery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/")
    public String greet() {
        return "+1 Hello Word 👌👌";
    }
}
