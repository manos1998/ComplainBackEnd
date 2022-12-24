package com.abctelecom.complain.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abctelecom.complain.models.ERole;
import com.abctelecom.complain.models.Role;
import com.abctelecom.complain.models.User;
import com.abctelecom.complain.payload.request.LoginRequest;
import com.abctelecom.complain.payload.request.PasswordResetRequest;
import com.abctelecom.complain.payload.request.SignupRequest;
import com.abctelecom.complain.payload.response.UserInfoResponse;
import com.abctelecom.complain.payload.response.MessageResponse;
import com.abctelecom.complain.repository.RoleRepository;
import com.abctelecom.complain.repository.UserRepository;
import com.abctelecom.complain.security.jwt.JwtUtils;
import com.abctelecom.complain.security.services.UserDetailsImpl;
import com.abctelecom.complain.security.services.UserDetailsServiceImpl;

import net.bytebuddy.utility.RandomString;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	UserDetailsServiceImpl userService;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(
				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(managerRole);

					break;
				case "eng":
					Role engineerRole = roleRepository.findByName(ERole.ROLE_ENGINEER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(engineerRole);

					break;
				case "fld":
					Role fieldworkerRole = roleRepository.findByName(ERole.ROLE_FIELDWORKER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(fieldworkerRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!"));
	}

	public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("support@abccomplain.com", "ABC Complain Support");
		helper.setTo(recipientEmail);

		String subject = "Here's the link to reset your password";

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + link
				+ "\">Change my password</a></p>" + "<br>" + "<p>Ignore this email if you do remember your password, "
				+ "or you have not made the request.</p>";

		helper.setSubject(subject);

		helper.setText(content, true);

		mailSender.send(message);
	}

	
	@PostMapping("/forgot_password")
	public ResponseEntity<?> forgetPasswordUser(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
		String token = RandomString.make(30);

		if (!userRepository.existsByEmail(passwordResetRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is not registered!"));
		}
		try {
			userService.updateResetPasswordToken(token, passwordResetRequest.getEmail());
			String resetPasswordLink = "http://localhost:8081/api/auth/reset_password/" + token;
			sendEmail(passwordResetRequest.getEmail(), resetPasswordLink);
			return ResponseEntity
					.ok(new MessageResponse("We have sent a reset password link to your email. Please check"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error" + e));
		}
	}

	@PostMapping("/reset_password")
	public ResponseEntity<?> forgetPasswordToken(@Valid @RequestBody PasswordResetRequest passwordResetRequest){
		if (!userRepository.existsByEmail(passwordResetRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is not registered!"));
		}
		User user = userService.getByResetPasswordToken(passwordResetRequest.getToken());
		if (user == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Invalid Token"));
		} else {
			userService.updatePassword(user, passwordResetRequest.getPassword());
			return ResponseEntity.ok(new MessageResponse("You have successfully changed your password."));
		}
	}
}