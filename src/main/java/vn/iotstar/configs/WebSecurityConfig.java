package vn.iotstar.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import vn.iotstar.filters.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    "users/**",
                                    "admin/home2",
                                    "users/forgot-password",
                                    "users/reset-password",
                                    "users/**",
                                    "users/verify-otp",
                                    "categories",
                                    "category/**",
                                    "address/**",
                                    "cart/**",
                                    "contact/**",
                                    "like/**",
                                    "order/**",
                                    "/**",
                                    "voucher/**",
                                    "templates/**",
                                    "admin/**"
                            )
                            .permitAll()
                            .requestMatchers("/css/dep.css", "/js/script.js", "/css/style.css", "/images/qr.png", "/fonts/**", "/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png")
                            .permitAll()
                            .anyRequest().authenticated();
                })
                .logout(logout -> logout
                        .logoutUrl("/users/logout") // Đường dẫn logout
                        .logoutSuccessUrl("/users/login") // Đường dẫn sau khi logout thành công
                        .invalidateHttpSession(true) // Xóa session khi logout
                        .deleteCookies("JSESSIONID") // Xóa cookie session
                );

        return http.build();
    }
}

