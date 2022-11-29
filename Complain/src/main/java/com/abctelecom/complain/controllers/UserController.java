package com.abctelecom.complain.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.User;
import com.abctelecom.complain.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/test")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String userAccess() {
		return "User Content.";
	}

	// Get User By ID or User Profile
	@GetMapping("/user/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<User> getUserDetails(@PathVariable("id") long id) {
		Optional<User> _user = userRepository.findById(id);
		return new ResponseEntity<>(_user.get(), HttpStatus.OK);
	}

	// Update User Profile
	@PutMapping("/user/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<User> upateUserDetail(@PathVariable("id") long id, @RequestBody User user) {
		Optional<User> userData = userRepository.findById(id);

		if (userData.isPresent()) {
			User _user = userData.get();
			_user.setFirstname(user.getFirstname());
			_user.setLastname(user.getLastname());
			_user.setPhone(user.getPhone());
			_user.setAddress(user.getAddress());
			_user.setPincode(user.getPincode());
			return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
