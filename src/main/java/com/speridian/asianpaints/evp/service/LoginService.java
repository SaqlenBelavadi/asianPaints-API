package com.speridian.asianpaints.evp.service;

import com.speridian.asianpaints.evp.dto.LoginResponse;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;

public interface LoginService {
	
	public LoginResponse login(LoginResponse loginResponse) throws EvpException;
	
	public LoginResponse refreshToken(LoginResponse loginResponse) throws EvpException;
	
	public LoginResponse switchProfile(LoginResponse loginResponse) throws EvpException;

	public void logout() throws EvpException;
	
	public  String validateUserLocationAndGet(Employee employee) throws EvpException;

}
