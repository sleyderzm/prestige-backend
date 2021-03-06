package silicon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import silicon.handler.HttpsEnforcer;
import silicon.handler.ValidateSession;
import silicon.service.SessionService;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SessionService sessionService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        http.addFilterAfter(
                new ValidateSession(sessionService), BasicAuthenticationFilter.class);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/redirect_dashboard_subscriber").allowedOrigins(
                        "*"
                ).allowedMethods(
                        "GET"
                );
                registry.addMapping("/**").allowedOrigins(
                        System.getenv("FRONT_URL")
                ).allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE"
                );
            }
        };
    }

    @Bean
    public Filter httpsEnforcerFilter(){
        return new HttpsEnforcer();
    }
}