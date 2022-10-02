package io.github.teardrop13.authentication.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterResponse {
    private String error;

    public static RegisterResponse success() {
        return new RegisterResponse(null);
    }

    public static RegisterResponse fail(String error) {
        return new RegisterResponse(error);
    }
}
