package pl.teardrop.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequest {

	private String password;
	private String email;
}
