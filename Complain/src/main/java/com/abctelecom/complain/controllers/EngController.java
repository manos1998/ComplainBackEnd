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
public class EngController {

	@Autowired
	private ComplainRepository complainRepository;

	@Autowired
	private UserRepository userRepository;
	
	//Get Engineer Board
	@GetMapping("/eng")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public String engineerAccess() {
		return "Engineer Board.";
	}
	
	//Get User
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	@GetMapping("/eng/user/{uId}")
	public ResponseEntity<User> getUser(@PathVariable("uId") Long id) {
		User user = userRepository.findById(id).orElse(null);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	// Get All Complain from Engineer ID
	@GetMapping("/eng/{eid}/getComplain")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<List<Complain>> getComplain(@PathVariable Long eid) {
		List<Complain> list = complainRepository.findAllComplainUser(eid);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	//Get Complain By ID Eng
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	@GetMapping("/eng/compUpdate/{idC}")
	public ResponseEntity<Complain> getComplainById(@PathVariable(value = "idC") Long idC) {
		Complain userComplains = complainRepository.findById(idC).orElse(null);
		return new ResponseEntity<>(userComplains, HttpStatus.OK);
	}

	//Assign Field Worker
	@PutMapping("/eng/compUpdate/{idE}")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<Complain> assignWorker(@PathVariable(value = "idE") Long uid, @RequestBody Long idC) {
		Complain _complain = complainRepository.findById(idC).orElse(null);
		User _user = userRepository.findById(uid).orElse(null);
		_user.addComplains(_complain);
		_complain.setActive(false);
		_complain.setStatus("ELEVATED");
		_complain.addWorkers(_user);
		userRepository.save(_user);
		complainRepository.save(_complain);
		return new ResponseEntity<>(_complain,HttpStatus.OK);
	}

	@GetMapping("/eng/getAllFieldWorker")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllFieldWorker() {
		List<User> userFieldWorker = userRepository.findByRolesFieldWorker();
		return new ResponseEntity<>(userFieldWorker, HttpStatus.OK);
	}
	
	//Update Complain As Resolved
	@PutMapping("/eng/complain/{comId}")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<String> UpdateComplain(@PathVariable Long comId) {
		Complain _complain = complainRepository.findById(comId).orElse(null);
		_complain.setStatus("RESOLVED");
		complainRepository.save(_complain);
		return new ResponseEntity<>("RESOLVED", HttpStatus.OK);
	}

//	----------------------------------------------------------------------------------
	
	// Set All Complain as active
	@PutMapping("/setallactive")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<List<Complain>> allUserComplainsSetActive() {
		List<Complain> userComplains = complainRepository.findAll();
		userComplains.forEach(complain -> complain.setActive(true));
		complainRepository.saveAll(userComplains);
		return new ResponseEntity<>(userComplains, HttpStatus.OK);
	};

	// Set all Complain as Resolved
	@PutMapping("/setallstatus")
	@PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
	public ResponseEntity<List<Complain>> allUserComplainsSetStatus() {
		List<Complain> userComplains = complainRepository.findAll();
		userComplains.forEach(complain -> complain.setStatus("RESOLVED"));
		complainRepository.saveAll(userComplains);
		return new ResponseEntity<>(userComplains, HttpStatus.OK);
	};


}