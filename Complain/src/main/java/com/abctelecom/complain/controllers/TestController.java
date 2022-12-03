package com.abctelecom.complain.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.Complain;
import com.abctelecom.complain.models.User;
import com.abctelecom.complain.repository.ComplainRepository;
import com.abctelecom.complain.repository.UserRepository;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@Autowired
	private ComplainRepository complainRepository;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	@GetMapping("/user/complain")
	@PreAuthorize("hasRole('USER')")
	public String userComplainAccess() {
		return "User Complain is Not Working";
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

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}

}