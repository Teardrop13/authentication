package pl.teardrop.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor()
@Getter
public class LoginResponse {

	private String sessionId;
}
