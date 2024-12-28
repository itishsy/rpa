package com.seebon.rpa.common.security.server;

import com.seebon.rpa.auth.JwtTokenConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 配置授权服务
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private JwtTokenConverter jwtTokenConverter;

    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    @Autowired
    private TokenStore tokenStore;

//    @Autowired
//    private JwtTokenEnhancer jwtTokenEnhancer;

//    @Autowired
//    private UserDetailsService userDetailsService;
//    private OAuth2RequestFactory oAuth2RequestFactory;
    /**
     * 配置客户端信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(new JdbcClientDetailsService(dataSource));
    }

    /**
     * 配置授权Token 的节点和Token 服务
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(jwtTokenEnhancer,jwtTokenConverter));
        endpoints.authenticationManager(authenticationManager) //认证管理器
                .tokenStore(tokenStore) //指定token存储
                .accessTokenConverter(jwtTokenConverter);
        endpoints.tokenEnhancer(tokenEnhancerChain);
        endpoints.exceptionTranslator(new CustomWebResponseExceptionTranslator());

        /*DefaultTokenServices tokenServices = (DefaultTokenServices) endpoints.getDefaultAuthorizationServerTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1));//一天
        endpoints.tokenServices(tokenServices);*/

    }

    /**
     * 获取Token的安全策略. 默认访问/oauth/token
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
//                .addTokenEndpointAuthenticationFilter(new TokenAuthenticationFilter(authenticationManager, oAuth2RequestFactory));;
    }


}