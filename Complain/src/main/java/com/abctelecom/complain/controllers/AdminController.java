package com.abctelecom.complain.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abctelecom.complain.models.ERole;
import com.abctelecom.complain.models.Role;
import com.abctelecom.complain.models.User;
import com.abctelecom.complain.payload.response.MessageResponse;
import com.abctelecom.complain.repository.RoleRepository;
import com.abctelecom.complain.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/test")
public class AdminController {
	public static final int USER_PER_PAGE = 5;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

//	@GetMapping("/admin")
//	@PreAuthorize("hasRole('ADMIN')")
//	public String adminAccess() {
//		return "Admin Board";
//	}

	// List all Users
	@GetMapping("/admin/roles")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Role>> getRoles() {
		List<Role> roles = roleRepository.findAll();
		return new ResponseEntity<>(roles, HttpStatus.OK);
	}

	// Get User Mod
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/user/{uId}")
	public ResponseEntity<User> getUser(@PathVariable("uId") Long id) {
		User user = userRepository.findById(id).orElse(null);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	// List all Users
	@GetMapping("/admin/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> getComplains() {
		List<User> users = (List<User>) userRepository.findAll();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

//	@GetMapping("/admin/users/page/{pageNum}")
//	@PreAuthorize("hasRole('ADMIN')")
//	public ResponseEntity<Page<User>> getUsersSortBy4(@PathVariable("pageNum") int pageNum) {
//		Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE);
//		return new ResponseEntity<>(userRepository.findAll(pageable), HttpStatus.OK);
//	}

	// Wanted to use Form User user not working
	/*
	 * @PreAuthorize("hasRole('ADMIN')")
	 * 
	 * @JsonIgnoreProperties
	 * 
	 * @PostMapping(value = "/admin/user/createUser", consumes = {
	 * MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	 * public ResponseEntity<?> upload(@RequestPart("username") String username,
	 * 
	 * @RequestPart("firstName") String firstName, @RequestPart("lastName") String
	 * lastName,
	 * 
	 * @RequestPart("email") String email, @RequestPart("enabled") String enabled,
	 * 
	 * @RequestPart("roles") String roles, @RequestPart("photos") String photos,
	 * 
	 * @RequestPart("password") String password, @RequestPart("file") MultipartFile
	 * file) throws IOException {
	 * 
	 * if (userRepository.existsByUsername(username)) { return
	 * ResponseEntity.badRequest().body(new
	 * MessageResponse("Error: Username is already taken!")); } if
	 * (userRepository.existsByEmail(email)) { return
	 * ResponseEntity.badRequest().body(new
	 * MessageResponse("Error: Email is already in use!")); } // Create new user's
	 * account String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	 * FileDB FileDBs = new FileDB(fileName, file.getContentType(),
	 * file.getBytes()); FileDB savedfile = fileRepository.save(FileDBs); String
	 * fileDownloadUri =
	 * ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/test/files/")
	 * .path(savedfile.getId()).toUriString(); System.out.println(fileDownloadUri);
	 * ERole erole = ERole.valueOf(roles); User _user = new User(username, email,
	 * encoder.encode(password), firstName, lastName,
	 * Boolean.parseBoolean(enabled)); Set<Role> _roles = new HashSet<>(); Role role
	 * = roleRepository.findByName(erole) .orElseThrow(() -> new
	 * RuntimeException("Error: Role is not found.")); _roles.add(role);
	 * _user.setRoles(_roles); _user.setPhotoId(savedfile.getId());
	 * _user.setPhotolink(fileDownloadUri); FileDBs.setUser(_user);
	 * userRepository.save(_user); return ResponseEntity.ok(new
	 * MessageResponse("User registered successfully!")); }
	 */	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/admin/user/updateUser/{uId}")
	public ResponseEntity<?> updateUser(@PathVariable("uId") Long id, @RequestBody User user) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			_user.setUsername(user.getUsername());
			_user.setEmail(user.getEmail());
			_user.setFirstname(user.getFirstname());
			_user.setLastname(user.getLastname());
			_user.setPhone(user.getPhone());
			_user.setPincode(user.getPincode());
			_user.setAddress(user.getAddress());
			_user.setRoles(user.getRoles());
			_user.setEnable(user.getEnable());
			System.out.println(_user);
			userRepository.save(_user);
			return new ResponseEntity<>(_user, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin/user/createUser")
	public ResponseEntity<?> createUser(@RequestBody User user) {
			User _user = new User(
					user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstname(),
					user.getLastname(),user.getPhone(),user.getPincode(),user.getAddress(),user.getRoles(), user.getEnable());
			System.out.println(_user.getEnable());
			userRepository.save(_user);
			return new ResponseEntity<>(_user, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/admin/user/deleteUser/{uId}")
	public ResponseEntity<?> deleteUser(@PathVariable("uId") Long id) {
		Optional<User> _user = userRepository.findById(id);
		if (_user.isPresent()) {
			userRepository.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("User Deleted Successfully"));
		} else {
			return ResponseEntity.ok(new MessageResponse("User not found"));
		}
	}

}