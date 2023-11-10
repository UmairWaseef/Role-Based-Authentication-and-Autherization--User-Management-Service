package com.example.userservice.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    @GetMapping
    public String post(){
        return "POST :: delivery controller";
    }

    @PutMapping
    public String put(){
        return "PUT :: Delivery controller";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE :: delivery controller";
    }

}
