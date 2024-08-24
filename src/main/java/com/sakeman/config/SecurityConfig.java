package com.sakeman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sakeman.service.CustomAuthenticationFailureHandler;
import com.sakeman.service.EmailService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

//    @Autowired
//    private UserDetailsService userDetailsService;
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
//    @Autowired
//    private EmailService emailService;

    /** 認証・認可設定 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(login -> login
                        .loginProcessingUrl("/login")    // ユーザー名・パスワードの送信先
                        .loginPage("/login")             // ログイン画面
                        .defaultSuccessUrl("/", true) // ログイン成功後のリダイレクト先
//                        .failureUrl("/login?error")      // ログイン失敗時のリダイレクト先
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
//                        .anyRequest().authenticated()    // その他はログイン必要
        ).rememberMe(me -> me.key("Unique and Secret SAKEMAN")
        ).httpBasic();  // HTTP Basic認証を有効にする


        return http.build();
    }

    /** ハッシュ化したパスワードの比較に使用する */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//    }
}