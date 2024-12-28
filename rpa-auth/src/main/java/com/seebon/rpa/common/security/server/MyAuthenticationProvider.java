package com.seebon.rpa.common.security.server;

import com.seebon.rpa.utils.EncryptUtil;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-24 18:56:36
 */
public class MyAuthenticationProvider extends DaoAuthenticationProvider {

    public MyAuthenticationProvider (UserDetailsService securityUserService) {
        setUserDetailsService(securityUserService) ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SneakyThrows
    @Override
    protected void additionalAuthenticationChecks (UserDetails userDetails, UsernamePasswordAuthenticationToken authenticationToken) throws AuthenticationException {
        if (authenticationToken.getCredentials() == null) {
            this.logger.debug( "Authentication failed:no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials","Bad credentials"));
        } else {
            String presentedPassword = authenticationToken.getCredentials().toString();
            //对登录密码进行解密
            try {
                presentedPassword = EncryptUtil.aesDecrypt(presentedPassword);
            }catch (Exception e) {

            }
            if (!passwordEncoder().matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug( "Authentication failed: password does not match stored value");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials","Bad credentials"));
            }
        }
    }
}
