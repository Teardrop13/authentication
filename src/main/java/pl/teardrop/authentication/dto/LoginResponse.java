package pl.teardrop.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
