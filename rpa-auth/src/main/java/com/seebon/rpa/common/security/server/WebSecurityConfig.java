package com.seebon.rpa.common.security.server;

import com.seebon.rpa.auth.JwtTokenConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity      //开启Web 保护功能
@EnableGlobalMethodSecurity(prePostEnabled = true)  //在方法上的保护功能
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.signing-key}")
    private String signingKey;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 配置验证的用户信息源和密码加密的策略
     * 采用的是密码验证。
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*auth.userDetailsService(userDetailsService)
            .passwordEncoder(new BCryptPasswordEncoder());*/
        auth.authenticationProvider(myAuthenticationProvider());
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenConverter());
    }

    @Bean
    public JwtTokenConverter jwtTokenConverter() {
        JwtTokenConverter jwtTokenConverter = new JwtTokenConverter();
        jwtTokenConverter.setSigningKey(signingKey);
        return jwtTokenConverter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public MyAuthenticationProvider myAuthenticationProvider() {
        MyAuthenticationProvider myAuthenticationProvider = new  MyAuthenticationProvider(userDetailsService);
        return myAuthenticationProvider;
    }
}
