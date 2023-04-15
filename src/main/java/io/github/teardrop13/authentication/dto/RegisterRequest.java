package io.github.teardrop13.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterRequest {

	private String username;
	private String password;
	private String password2;
	private String email;
}
