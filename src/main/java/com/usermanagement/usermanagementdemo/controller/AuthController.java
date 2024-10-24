package com.usermanagement.usermanagementdemo.controller;

import com.usermanagement.usermanagementdemo.dto.LoginDto;
import com.usermanagement.usermanagementdemo.dto.ResponseMessage;
import com.usermanagement.usermanagementdemo.dto.SignUpDto;
import com.usermanagement.usermanagementdemo.entity.Role;
import com.usermanagement.usermanagementdemo.entity.User;
import com.usermanagement.usermanagementdemo.repository.RoleRepository;
import com.usermanagement.usermanagementdemo.repository.UserRepository;
import com.usermanagement.usermanagementdemo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

//    @PostMapping("/signin")
//    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                loginDto.getUsernameOrEmail(), loginDto.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
//    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseMessage> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = tokenProvider.genrateToken(loginDto.getUsernameOrEmail());

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK.value(), "User signed in successfully", token);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody SignUpDto signUpDto){

        // add check for username exists in a DB
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Username is already taken"), HttpStatus.BAD_REQUEST);

        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Email is already taken"), HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Role not found."));
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage(HttpStatus.OK.value(), "User registered successfully"), HttpStatus.OK);

    }
}