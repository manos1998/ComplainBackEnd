package com.abctelecom.complain.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.Complain;
import com.abctelecom.complain.repository.ComplainRepository;


@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/test/user/complain")
public class ComplainController {

	@Autowired
	ComplainRepository complainRepository;
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/complains")
	public ResponseEntity<List<Complain>> getAllComplains(@RequestParam(required = false) String type) {
		try {
			List<Complain> complains = new ArrayList<Complain>();

			if (type == null)
				complainRepository.findAll().forEach(complains::add);
			else
				complainRepository.findByTypeContaining(type).forEach(complains::add);

			if (complains.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(complains, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/complains/{id}")
	public ResponseEntity<Complain> getComplainById(@PathVariable("id") long id) {
		Optional<Complain> complainDate = complainRepository.findById(id);

		if (complainDate.isPresent()) {
			return new ResponseEntity<>(complainDate.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/complains")
	public ResponseEntity<Complain> createComplain(@RequestBody Complain complain) {
		try {
			Complain _complain = complainRepository.save(new Complain(complain.isActive(), complain.getDetails(), complain.getType(), complain.getStatus()));
			return new ResponseEntity<>(_complain, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/complains/{id}")
	public ResponseEntity<Complain> updateComplain(@PathVariable("id") long id, @RequestBody Complain complain) {
		Optional<Complain> complainDate = complainRepository.findById(id);

		if (complainDate.isPresent()) {
			Complain _complain = complainDate.get();
			_complain.setType(complain.getType());
			_complain.setDetails(complain.getDetails());
			_complain.setActive(complain.isActive());
			_complain.setActive(false);
			return new ResponseEntity<>(complainRepository.save(_complain), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/complains/{id}")
	public ResponseEntity<HttpStatus> deleteComplain(@PathVariable("id") long id) {
		try {
			complainRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/complains")
	public ResponseEntity<HttpStatus> deleteAllComplains() {
		try {
			complainRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/complains/active")
	public ResponseEntity<List<Complain>> findByPublished() {
		try {
			List<Complain> complains = complainRepository.findByActive(true);

			if (complains.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(complains, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}