package com.tang.clientapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author weepppp 2022/7/22 20:12
 * 授权服务器授权成功，携带code重定向到index.html
 **/
@Controller
public class HelloController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/index.html")
    public String hello(String code, Model model){
        if (code != null){
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("code",code);
            map.add("client_id","lisi");
            map.add("client_secret","456");
            map.add("redirect_uri","http://localhost:8082/index.html");
            map.add("grant_type","authorization_code");
            Map<String, String> resp = restTemplate.postForObject("http://localhost:8080/oauth/token", map, Map.class);
            String access_token = resp.get("access_token");
            System.out.println(access_token);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization","Bearer "+access_token);
            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8081/admin/hello", HttpMethod.GET, entity, String.class);
            model.addAttribute("msg",responseEntity.getBody());

        }
        return "index";
    }

}
