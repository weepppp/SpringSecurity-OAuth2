package com.tang.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * @author weepppp 2022/7/22 13:34
 * 配置授权服务器生成的token的存储位置，在这里我们把它都存在内存中
 **/
@Configuration
public class AccessTokenConfig {

    //TODO 全部写完后可以测试下 把这个方法移到授权服务器 测试是否还是正常运行

    @Bean
    TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }
}
