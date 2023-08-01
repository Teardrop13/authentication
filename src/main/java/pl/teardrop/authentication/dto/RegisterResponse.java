package pl.teardrop.authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RegisterResponse {

	private String error;

	public static RegisterResponse success() {
		return new RegisterResponse(null);
	}

	public static RegisterResponse fail(String error) {
		return new RegisterResponse(error);
	}
}
