package io.github.teardrop13.authentication.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginResponse {

	private String sessionId;
	private String error;

	public static LoginResponse success(String sessionId) {
		return new LoginResponse(sessionId, null);
	}

	public static LoginResponse fail(String error) {
		return new LoginResponse(null, error);
	}
}
