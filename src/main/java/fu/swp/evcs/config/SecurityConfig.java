package fu.swp.evcs.config;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * ✅ SecurityConfig - Cấu hình Spring Security đơn giản
 * 
 * - Không cần CustomUserDetailsService (User đã implement UserDetails)
 * - Không cần JsonAuthenticationFilter (dùng Controller endpoint)
 * - Không cần SecurityUtils (dùng @AuthenticationPrincipal)
 * - CORS tập trung - không cần @CrossOrigin ở controller
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CORS configuration tập trung
                .cors(Customizer.withDefaults())
                
                // ✅ CSRF protection cho session-based authentication
                .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    // Exempt register và login endpoints
                    .ignoringRequestMatchers("/api/auth/register", "/api/auth/login")
                )
                
                // ✅ Session management
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1) // Chỉ cho phép 1 session/user
                    .maxSessionsPreventsLogin(false) // Session mới thay thế session cũ
                )
                
                // ✅ Authorization rules
                .authorizeHttpRequests(auth -> auth
                    // Public endpoints
                    .requestMatchers(
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api/auth/**" // Login, register, logout đều public
                    ).permitAll()
                    
                    // Admin endpoints - chỉ ROLE_ADMIN
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    
                    // Tất cả endpoints khác cần authentication
                    .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * ✅ Password encoder với BCrypt strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * ✅ Authentication manager - cần cho login endpoint
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * ✅ Authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    /**
     * ✅ CORS configuration cho Next.js frontend
     * Loại bỏ nhu cầu @CrossOrigin ở từng controller
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Cho phép frontend Next.js
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        
        // Cho phép tất cả HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Cho phép tất cả headers
        config.setAllowedHeaders(List.of("*"));
        
        // Cho phép gửi credentials (cookies, session)
        config.setAllowCredentials(true);
        
        // Expose CSRF token header cho frontend
        config.setExposedHeaders(List.of("X-XSRF-TOKEN"));
        
        // Cache preflight request 1 giờ
        config.setMaxAge(Duration.ofHours(1));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
