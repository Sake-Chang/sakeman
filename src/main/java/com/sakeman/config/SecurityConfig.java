package com.sakeman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import com.sakeman.service.CustomAuthenticationFailureHandler;
import com.sakeman.service.EmailService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    /** 認証・認可設定 */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(login -> login
                        .loginProcessingUrl("/login")    // ユーザー名・パスワードの送信先
                        .loginPage("/login")             // ログイン画面
                        .defaultSuccessUrl("/", true) // ログイン成功後のリダイレクト先
                        .failureHandler(customAuthenticationFailureHandler)
                        .usernameParameter("email")
                        .permitAll()                     // ログイン画面は未ログインでアクセス可
        ).logout(logout -> logout
                        .logoutSuccessUrl("/login")      // ログアウト後のリダイレクト先
        ).authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()   //CSS等はOK
                        .antMatchers("/signup").permitAll()
                        .mvcMatchers("/admin/**").hasAnyAuthority("管理者")
                        .antMatchers("/actuator/**").hasAnyAuthority("管理者")  // Actuatorには認証が必要
                        .anyRequest().permitAll()
        ).rememberMe(me -> me.key("Unique and Secret SAKEMAN")
        ).csrf(csrf -> csrf.ignoringAntMatchers("/evict-cache")
        ).httpBasic(Customizer.withDefaults());  // HTTP Basic認証を有効にする

        return http.build();
    }

    /** ハッシュ化したパスワードの比較に使用する */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}