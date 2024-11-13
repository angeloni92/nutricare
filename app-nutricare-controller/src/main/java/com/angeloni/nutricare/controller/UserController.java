package com.angeloni.nutricare.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.service.UserService;
import com.angeloni.nutricare.util.JwtTokenUtil;

@RestController
@RequestMapping("/auth")
public class UserController {
	
	@Autowired
    private UserService userService; 

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }
    
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginDto loginDto) {
        return userService.loginUser(loginDto);
    }

}
