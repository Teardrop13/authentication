package pl.teardrop.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDTO {

    private String username;
    private String password;
    private String email;
}
