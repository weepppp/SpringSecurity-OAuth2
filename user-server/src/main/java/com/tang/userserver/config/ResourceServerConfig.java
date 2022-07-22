package com.tang.userserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * @author weepppp 2022/7/22 19:31
 * 配置资源服务器
 * 相关功能： 校验token是否正确+配置资源拦截规则
 **/
@Configuration

//@EnableResourceServer:配置资源服务相关的安全配置

@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 我们配置了一个 RemoteTokenServices 的实例，这是因为资源服务器和授权服务器是分开的
     * 资源服务器接收请求资源携带的token时，需要访问授权服务器特定端口地址 去校验token是否正确
     * @return
     */
    @Bean
    RemoteTokenServices tokenServices() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
        tokenServices.setClientId("lisi");
        tokenServices.setClientSecret("456");
        return tokenServices;
    }

    /**
     * 开启资源服务配置
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("res1").tokenServices(tokenServices());
    }

    /**
     * 配置一下资源的拦截规则
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .anyRequest().authenticated();
    }
}
