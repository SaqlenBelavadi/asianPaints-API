package com.speridian.asianpaints.evp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.speridian.asianpaints.evp.dto.GenericResponse;
import com.speridian.asianpaints.evp.dto.LoginResponse;
import com.speridian.asianpaints.evp.dto.Response;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.service.LoginService;

@RestController
@RequestMapping("/api/evp/v1/")
public class UserLoginController {

	@Autowired
	private LoginService loginService;

	@PostMapping("/Login")
	public Object login(@RequestBody LoginResponse loginResponse) {

		try {
			loginResponse = loginService.login(loginResponse);
			return ResponseEntity.ok(loginResponse);
		} catch (EvpException e) {
			Response response = Response.builder().message(e.getMessage()).build();

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}

	@PostMapping("/RefreshToken")
	public Object refreshToken(@RequestBody LoginResponse loginResponse) {

		try {
			loginResponse = loginService.refreshToken(loginResponse);
			return ResponseEntity.ok(loginResponse);
		} catch (EvpException e) {
			Response response = Response.builder().message("UnAuthorized").reason(e.getMessage()).build();

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}

	@PostMapping("/SwitchProfile")
	public Object switchProfile(@RequestBody LoginResponse loginResponse) {

		try {
			loginResponse = loginService.switchProfile(loginResponse);
			return ResponseEntity.ok(loginResponse);
		} catch (EvpException e) {
			Response response = Response.builder().message("UnAuthorized").reason(e.getMessage()).build();

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}

	@PostMapping("/Logout")
	public ResponseEntity<GenericResponse> logout() {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			loginService.logout();
			genericResponse.setData("Logout Successfull");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

}
