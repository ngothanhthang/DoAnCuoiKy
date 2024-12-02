package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import vn.iostar.configs.MySiteMeshFilter;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "vn.iotstar")
@EnableJpaRepositories("vn.iotstar.repository")
public class DoAnCuoiKyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoAnCuoiKyApplication.class, args);
	}
	@Bean
	public FilterRegistrationBean<MySiteMeshFilter> siteMeshFilter() {
		FilterRegistrationBean<MySiteMeshFilter> filterRegistrationBean
			=new FilterRegistrationBean<MySiteMeshFilter>();
		filterRegistrationBean.setFilter(new MySiteMeshFilter()); // adding sitemesh filter ??
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/admin/**").permitAll()  // Cho phép tất cả truy cập vào các trang /admin/**
                                .requestMatchers("/customer/**").permitAll()  // Cho phép tất cả truy cập vào các trang /customer/**
                                .anyRequest().permitAll()  // Cho phép tất cả các yêu cầu khác mà không cần xác thực
                )
                .formLogin(login -> login.disable())  // Vô hiệu hóa trang đăng nhập của Spring Security
                .httpBasic(basic -> basic.disable())  // Tắt Basic Authentication nếu không cần thiết
                .build();
	}


}
