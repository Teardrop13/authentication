package pl.teardrop.authentication.session;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.teardrop.authentication.exceptions.FailedRetrievingAuthorizationToken;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationUtil {

	public static String getToken(String bearer) throws FailedRetrievingAuthorizationToken {
		if (Strings.isNullOrEmpty(bearer) || bearer.length() < 7) {
			throw new FailedRetrievingAuthorizationToken();
		}

		return bearer.substring(7);
	}
}
