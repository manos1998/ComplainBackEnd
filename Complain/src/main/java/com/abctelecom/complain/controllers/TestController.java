package com.abctelecom.complain.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER')")
  public String userAccess() {
    return "User Content.";
  }
  
  @GetMapping("/engineer")
  @PreAuthorize("hasRole('ENGINEER')")
  public String engineerAccess() {
    return "Engineer Board.";
  }
  
  @GetMapping("/fieldworker")
  @PreAuthorize("hasRole('FIELDWORKER')")
  public String fieldworkerAccess() {
    return "FieldWorker Board.";
  }

  @GetMapping("/manager")
  @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
  public String managerAccess() {
    return "Manager Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }
  
}