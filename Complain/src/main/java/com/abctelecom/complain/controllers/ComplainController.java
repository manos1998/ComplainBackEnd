package com.abctelecom.complain.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.Complain;
import com.abctelecom.complain.models.User;
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

	/*
	 * @GetMapping("/user/{userId}/complains")
	 * 
	 * @PreAuthorize("hasRole('USER')") public ResponseEntity<User>
	 * getUserDetails(@PathVariable("id") long id) { Optional<User> _user =
	 * userRepository.findById(id); return new ResponseEntity<>(_user.get(),
	 * HttpStatus.OK); }
	 */

	// Get Complain By User Id
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user/{userId}/complains")
	public ResponseEntity<List<Complain>> getAllComplainsByUserId(@PathVariable(value = "userId") Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new Error("User not Found By Id" + userId);
		}

		List<Complain> complains = complainRepository.findByUserId(userId);
		return new ResponseEntity<>(complains, HttpStatus.OK);
	}

	// Get Complain By Id
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/complains/{id}")
	public ResponseEntity<Complain> getComplainById(@PathVariable(value = "id") Long id) {
		Complain complain = complainRepository.findById(id).orElseThrow();

		return new ResponseEntity<>(complain, HttpStatus.OK);
	}

	// Create Complain
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/user/{userId}/complains")
	public ResponseEntity<Complain> createComplain(@PathVariable(value = "userId") Long userId,
			@RequestBody Complain complainRequest) {
		Complain complain = new Complain(complainRequest.isActive(), complainRequest.getDetails(),
				complainRequest.getType(), complainRequest.getStatus());
		User _user = userRepository.findById(userId).orElseThrow();
		_user.addUserComplain(complain);
		complain.setPincode(_user.getPincode());
		complain.setuId(userId);
		userRepository.save(_user);
		return new ResponseEntity<>(complainRepository.save(complain), HttpStatus.CREATED);
	}

	/*
	 * @PutMapping("/complains/{id}") public ResponseEntity<Complain>
	 * updateComplainFeedback(@PathVariable(value = "id") Long id, @RequestBody
	 * String feedback){ Complain _complain =
	 * complainRepository.findById(id).orElse(null);
	 * _complain.setFeedback(feedback); return new ResponseEntity<>
	 * (complainRepository.save(_complain), HttpStatus.OK); }
	 */

	// Update Feedback
	@PutMapping("/complain/{id}")
	public ResponseEntity<Complain> updateComplain(@PathVariable(value = "id") Long id,
			@RequestBody Complain complain) {
		Complain _complain = complainRepository.findById(id).orElse(null);
		_complain.setFeedback(complain.getFeedback());
		return new ResponseEntity<>(complainRepository.save(_complain), HttpStatus.OK);
	}
	

}