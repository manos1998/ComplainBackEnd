package com.abctelecom.complain.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.Complain;
import com.abctelecom.complain.repository.ComplainRepository;
import com.abctelecom.complain.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/test")
public class ComplainController {

	@Autowired
	ComplainRepository complainRepository;

	@Autowired
	UserRepository userRepository;

//	@GetMapping("/user/{userId}/complains")
//	@PreAuthorize("hasRole('USER')")
//	public ResponseEntity<User> getUserDetails(@PathVariable("id") long id) {
//		Optional<User> _user = userRepository.findById(id);
//		return new ResponseEntity<>(_user.get(), HttpStatus.OK);
//	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user/{userId}/complains")
	public ResponseEntity<List<Complain>> getAllComplainsByUserId(@PathVariable(value = "userId") Long userId) {
		if (!complainRepository.existsById(userId)) {
			throw new Error("User not Found By Id" + userId);
		}

		List<Complain> complains = complainRepository.findByUserId(userId);
		return new ResponseEntity<>(complains, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/complains/{id}")
	public ResponseEntity<Complain> getComplainByUserId(@PathVariable(value = "id") Long id) {
		Complain complain = complainRepository.findById(id).orElseThrow();

		return new ResponseEntity<>(complain, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/user/{userId}/complains")
	public ResponseEntity<Complain> createComplain(@PathVariable(value = "userId") Long userId,
			@RequestBody Complain complainRequest) {
		Complain complain = new Complain(complainRequest.isActive(), complainRequest.getDetails(), complainRequest.getType(), complainRequest.getStatus());
		userRepository.findById(userId).map(user -> {
			complain.setUser(user);
			return complainRepository.save(complain);
		}).orElseThrow();

		return new ResponseEntity<>(complain, HttpStatus.CREATED);
	}


}