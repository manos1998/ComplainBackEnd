package com.abctelecom.complain.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.Complain;
import com.abctelecom.complain.models.User;
import com.abctelecom.complain.repository.ComplainRepository;
import com.abctelecom.complain.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/test")
public class FieldController {

	@Autowired
	private ComplainRepository complainRepository;

	@Autowired
	private UserRepository userRepository;
	
	//Get Engineer Board
	@GetMapping("/fld")
	@PreAuthorize("hasRole('FIELDWORKER') or hasRole('ADMIN')")
	public String engineerAccess() {
		return "FIELDWORKER Board.";
	}
	
	//Get User
	@PreAuthorize("hasRole('FIELDWORKER') or hasRole('ADMIN')")
	@GetMapping("/fld/user/{uId}")
	public ResponseEntity<User> getUser(@PathVariable("uId") Long id) {
		User user = userRepository.findById(id).orElse(null);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	// Get All Complain from fld ID
	@GetMapping("/fld/{eid}/getComplain")
	@PreAuthorize("hasRole('FIELDWORKER') or hasRole('ADMIN')")
	public ResponseEntity<List<Complain>> getComplain(@PathVariable Long eid) {
		List<Complain> list = complainRepository.findAllComplainUser(eid);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	//Get Complain By ID Eng
	@PreAuthorize("hasRole('FIELDWORKER') or hasRole('ADMIN')")
	@GetMapping("/fld/compUpdate/{idC}")
	public ResponseEntity<Complain> getComplainById(@PathVariable(value = "idC") Long idC) {
		Complain userComplains = complainRepository.findById(idC).orElse(null);
		return new ResponseEntity<>(userComplains, HttpStatus.OK);
	}
	
	//Update Complain As Resolved
	@PutMapping("/fld/complain/{comId}")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<String> UpdateComplain(@PathVariable Long comId) {
		Complain _complain = complainRepository.findById(comId).orElse(null);
		_complain.setStatus("RESOLVED");
		complainRepository.save(_complain);
		return new ResponseEntity<>("RESOLVED", HttpStatus.OK);
	}

	
}