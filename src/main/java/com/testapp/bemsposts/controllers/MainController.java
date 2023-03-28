package com.testapp.bemsposts.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// *** ENTRY POINT CONTROLLER ***
@Controller
public class MainController {
  @GetMapping("/")
  public String mainGet() {
    return "index";
  }
}
