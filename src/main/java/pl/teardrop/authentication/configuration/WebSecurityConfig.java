package pl.teardrop.authentication.configuration;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.teardrop.authentication.session.SessionFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final SessionFilter sessionFilter;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors().and()
				.csrf().disable()//todo włączyć
				.exceptionHandling().authenticationEntryPoint(
						(request, response, authException) -> response.sendError(response.getStatus(), authException.getMessage())
				).and()
				.addFilterBefore(
						sessionFilter,
						UsernamePasswordAuthenticationFilter.class
				)
				.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/api/auth/login"), new AntPathRequestMatcher("/api/auth/register")).permitAll()
				.and().authorizeHttpRequests().anyRequest().authenticated();

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
