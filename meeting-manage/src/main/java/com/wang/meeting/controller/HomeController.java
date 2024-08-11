package com.wang.meeting.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@Slf4j
public class HomeController {

    @GetMapping("/liveness")
    public ResponseEntity<String> hello() {
        log.info("debug mode");
        return ResponseEntity.ok().body("ok");
    }
}

