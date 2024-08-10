package com.wang.meeting.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/liveness")
    public ResponseEntity<String> hello() {

        return ResponseEntity.ok().body("ok");
    }
}

