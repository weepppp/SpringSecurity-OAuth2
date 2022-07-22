package com.tang.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author weepppp 2022/7/22 13:49
 * 授权服务器
 * 相关功能：校验code（用户+第三方客户端），生成发送code、token，存储code、token，给资源服务器提供验证token的端口
 **/
@Configuration
@EnableAuthorizationServer   //@EnableAuthorizationServer：开启授权服务器的自动化配置
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {


    //注入token的存储对象
    @Autowired
    TokenStore tokenStore;

    @Autowired
    ClientDetailsService clientDetailsService;

    /**
     * 配置授权服务器生成的code的存储位置，在这里我们把它都存在内存中
     * @return
     */
    @Bean
    AuthorizationCodeServices authorizationCodeServices(){
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * 配置 生成的Token的一些基本信息
     * 例如 Token 是否支持刷新、Token 的存储位置、Token 的有效期以及刷新 Token 的有效期等等。
     */
    @Bean
    AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 2);
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
        return tokenServices;
    }

    /**
     * AuthorizationServerSecurityConfigurer 用来配置令牌端点的安全约束，也就是这个端点谁能访问，谁不能访问
     * checkTokenAccess 是指一个 Token 校验的端点，这个端点我们设置为可以直接访问
     * 在后面，当资源服务器收到 Token 之后，需要去校验 Token 的合法性，就会访问这个端点
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()").allowFormAuthenticationForClients();
    }

    /**
     * 配置客户端的详细信息，在用户授权登录时进行第三方客户端校验
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("lisi")
                .secret(new BCryptPasswordEncoder().encode("456"))
                .resourceIds("res1")
                .authorizedGrantTypes("authorization_code","refresh_token")
                .scopes("all")
                .redirectUris("http://localhost:8082/index.html");
    }

    /**
     * 配置令牌的访问端点和令牌服务，即到哪里去验证code和token的正确性
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices()).tokenServices(tokenServices());
    }
}
