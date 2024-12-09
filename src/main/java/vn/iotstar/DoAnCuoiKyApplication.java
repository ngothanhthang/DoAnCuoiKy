package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@SpringBootApplication
//@SpringBootApplication(scanBasePackages = "vn.iotstar")
//@EnableJpaRepositories("vn.iotstar.repository")
public class DoAnCuoiKyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoAnCuoiKyApplication.class, args);
	}
}
