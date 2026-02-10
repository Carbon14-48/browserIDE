package com.Controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.browserIDE.backend.models.User;

@RestController
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {

        return null;
    }

    @PostMapping("/register")
    public Map<String, String> postMethodName(@RequestBody User user) {

        return null;
    }

}
