package com.speridian.asianpaints.evp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="EVP_USER_LOGIN")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin extends AbstractEntity{
	
	@Column(name = "USERNAME")
	private String username;
	
	@Column(name = "ACCESS_TOKEN")
	private String accessToken;
	
	@Column(name = "REFRESH_TOKEN")
	private String refreshToken;
	
	@Column(name = "ACCESS_EXPIRY_TIME")
	private LocalDateTime accessExpiryTime;
	
	@Column(name = "REFRESH_TOKEN_EXPIRY_TIME")
	private LocalDateTime refreshTokenExpiryTime;

}
