package com.example.userservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @GetMapping
    public String post(){
        return "GET :: customer controller";
    }

    @PutMapping
    public String put(){
        return "PUT :: customer controller";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE :: customer controller";
    }

}
