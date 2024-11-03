package com.hilmatrix.montrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<html><body><b style='color:red; font-size:36px;'>Hilmatrix Montrack</b></body></html>";
    }
}