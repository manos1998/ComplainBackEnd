package com.abctelecom.complain.controllers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.abctelecom.complain.models.ERole;
import com.abctelecom.complain.models.Role;
import com.abctelecom.complain.models.User;
import com.abctelecom.complain.repository.ComplainRepository;
import com.abctelecom.complain.repository.RoleRepository;
import com.abctelecom.complain.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/test")
public class ModController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ComplainRepository complainRepository;

	@Autowired
	private RoleRepository roleRepository;

	// Get Moderator Board
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public String managerAccess() {
		return "Manager Board.";
	}

	// List all Complains
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@GetMapping("/mod/complains")
	public ResponseEntity<List<Complain>> getComplains() {
		List<Complain> complains = complainRepository.findAll();
		return new ResponseEntity<>(complains, HttpStatus.OK);
	}

	// Get User Mod
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@GetMapping("/mod/user/{uId}")
	public ResponseEntity<User> getUser(@PathVariable("uId") Long id) {
		User user = userRepository.findById(id).orElse(null);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	// Get User Mod
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@PutMapping("/mod/userroleupdate/{uId}")
	public ResponseEntity<User> updateUser(@PathVariable("uId") Long id, @RequestBody User user) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			_user.setFirstname(user.getFirstname());
			_user.setLastname(user.getLastname());
			_user.setPhone(user.getPhone());
			_user.setPincode(user.getPincode());
			_user.setAddress(user.getAddress());
			return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@PutMapping("/mod/userRole/{uId}")
	public ResponseEntity<User> updateRoles(@PathVariable("uId") Long id, @RequestBody String[] array) {
		Set<Role> roles = new HashSet<>();
		for (int i = 0; i < array.length; i++) {
			Role role = roleRepository.findById(Integer.parseInt(array[i])).orElse(null);
			roles.add(role);
		}
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			System.out.print("Roles" + roles);
			_user.setRoles(roles);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Get All Role List
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@GetMapping("/mod/roles")
	public ResponseEntity<List<Role>> getRole() {
		List<Role> role = roleRepository.findAll();
		return new ResponseEntity<>(role, HttpStatus.OK);
	}
	
	@GetMapping("/mod/user/{uId}/getComplains")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<List<Complain>> getComplain(@PathVariable Long uId) {
		List<Complain> list = complainRepository.findAllComplainUser(uId);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


	// Get User By Complain Id
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@GetMapping("/mod/complains/{id}/user")
	public ResponseEntity<User> getComplainByUserId(@PathVariable(value = "id") Long id) {
		Complain complain = complainRepository.findById(id).orElseThrow();
		User _user = complain.getUser();
		return new ResponseEntity<>(_user, HttpStatus.OK);
	}

	// Tested
	@GetMapping("/mod/getAllEngineer")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllEngineer() {
		List<User> userEngineer = userRepository.findByRolesEngineer();
		return new ResponseEntity<>(userEngineer, HttpStatus.OK);
	}

	// Tested
	@GetMapping("/mod/getAllFieldWorker")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllFieldWorker() {
		List<User> userEngineer = userRepository.findByRolesFieldWorker();
		return new ResponseEntity<>(userEngineer, HttpStatus.OK);
	}

	//Tested
	@GetMapping("/mod/getAllUsers")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> user = userRepository.findByRolesUsers();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	// Tested Get Complain By ID Mod
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@GetMapping("/mod/compUpdate/{idC}")
	public ResponseEntity<Complain> getComplainById(@PathVariable(value = "idC") Long idC) {
		Complain userComplains = complainRepository.findById(idC).orElse(null);
		return new ResponseEntity<>(complainRepository.save(userComplains), HttpStatus.OK);
	}

	//Tested
	@PutMapping("/mod/compUpdate/{idE}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<Complain> assignWorker(@PathVariable(value = "idE") Long uid, @RequestBody Long idC) {
		Complain _complain = complainRepository.findById(idC).orElse(null);
		User _user = userRepository.findById(uid).orElse(null);
		_user.addComplains(_complain);
		_complain.setActive(false);
		_complain.setStatus("WIP");
		_complain.addWorkers(_user);
		userRepository.save(_user);
		complainRepository.save(_complain);
		return new ResponseEntity<>(_complain, HttpStatus.OK);
	}

	// ?????????????????????????????????????????????????///?
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	@PutMapping("/mod/complains")
	public ResponseEntity<List<Complain>> getComplainandUpdate() {
		List<Complain> userComplains = complainRepository.findAll();
		userComplains.forEach(complain -> complain.setStatus("RESOLVED"));
		userComplains.forEach(complain -> complain.setActive(false));
		return new ResponseEntity<>(userComplains, HttpStatus.OK);
	}

}