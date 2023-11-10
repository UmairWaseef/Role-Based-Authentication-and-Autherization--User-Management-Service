package com.example.userservice.controller;

import com.example.userservice.User.User;
import com.example.userservice.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping
    public String get(){
        return "GET :: admin controller";

    }


    @PostMapping
    public String post(){
        return "POST :: admin controller";
    }

    @PutMapping
    public String put(){
        return "PUT :: admin controller";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE :: admin controller";
    }


    @Autowired
    private UserRepository userRepository;

    @PostMapping("/decide/{userId}/accept")
    public ResponseEntity<String> acceptUser(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActivationStatus("ACCEPTED");
            userRepository.save(user);
            return ResponseEntity.ok("User accepted.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/decide/{userId}/reject")

    public ResponseEntity<String> rejectUser(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActivationStatus("REJECTED");
            userRepository.save(user);
            return ResponseEntity.ok("User rejected.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
