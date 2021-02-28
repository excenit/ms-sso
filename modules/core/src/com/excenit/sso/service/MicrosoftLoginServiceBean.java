package com.excenit.sso.service;

import com.excenit.sso.config.SsoConfig;
import com.excenit.sso.entity.LoginUserData;
import com.google.gson.Gson;
import com.haulmont.cuba.core.global.AppBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service(MicrosoftLoginService.NAME)
public class MicrosoftLoginServiceBean implements MicrosoftLoginService {
    private final Logger log = LoggerFactory.getLogger(MicrosoftLoginServiceBean.class);

    private String graphAccessToken;
    private String graphRefreshToken;
    private LocalDateTime time;

    @Override
    public String getGraphAccessToken() {
        return graphAccessToken;
    }

    public void setGraphAccessToken(String graphAccessToken) {
        this.graphAccessToken = graphAccessToken;
    }

    @Override
    public String getGraphRefreshToken() {
        return graphRefreshToken;
    }

    public void setGraphRefreshToken(String graphRefreshToken) {
        this.graphRefreshToken = graphRefreshToken;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public LoginUserData getMicrosoftUserData(String code){

        String apiUrl = AppBeans.get(SsoConfig.class).getGraphApiBaseURL()+"me";
        String responseMethod = "GET";

        Gson gson = new Gson();
        LoginUserData userData = new LoginUserData();
        String tokenJson = AppBeans.get(GraphApiAuthenticationService.class).retrieveAccessToken(code,true);
        Map<String,String> tokenMap = gson.fromJson(tokenJson,Map.class);
        String token = "Bearer "+tokenMap.get("access_token");
        setGraphAccessToken(tokenMap.get("access_token"));
        setGraphRefreshToken(tokenMap.get("refresh_token"));
        setTime(LocalDateTime.now());


        log.info(token);
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",token);
        header.put("Content-Type","application/json");
        header.put("Accept","application/json");

        String response = AppBeans.get(GraphApiAuthenticationService.class)
                .postRequestToRestService(responseMethod,header,apiUrl,null);
        log.info(response);
        Map<String,String> responseMap = gson.fromJson(response,Map.class);

        userData.setId(responseMap.get("id"));
        userData.setEmail(responseMap.get("mail"));
        userData.setName(responseMap.get("displayName"));

        return userData;
    }

    @Override
    public String getMicrosoftUserDetails(String code){

        String apiUrl = AppBeans.get(SsoConfig.class).getGraphApiBaseURL()+"me";
        String responseMethod = "GET";

        Gson gson = new Gson();
        LoginUserData userData = new LoginUserData();
        String tokenJson = AppBeans.get(GraphApiAuthenticationService.class)
                .retrieveAccessToken(code,true);
        Map<String,String> tokenMap = gson.fromJson(tokenJson,Map.class);
        String token = "Bearer "+tokenMap.get("access_token");
        setGraphAccessToken(tokenMap.get("access_token"));
        setGraphRefreshToken(tokenMap.get("refresh_token"));
        setTime(LocalDateTime.now());


        log.info(token);
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",token);
        header.put("Content-Type","application/json");
        header.put("Accept","application/json");

        String response = AppBeans.get(GraphApiAuthenticationService.class)
                .postRequestToRestService(responseMethod,header,apiUrl,null);
        log.info(response);


        return response;
    }

    @Override
    public String getLoginUrl(){
        return AppBeans.get(GraphApiAuthenticationService.class).getAuthorizationCodeURL();
    }

}