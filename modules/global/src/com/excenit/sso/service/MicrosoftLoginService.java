package com.excenit.sso.service;

import com.excenit.sso.entity.LoginUserData;

import java.time.LocalDateTime;

public interface MicrosoftLoginService {
    String NAME = "sso_MicrosoftLoginService";

    String getGraphAccessToken();

    String getGraphRefreshToken();

    LocalDateTime getTime();

    LoginUserData getMicrosoftUserData(String code);

    String getMicrosoftUserDetails(String code);

    String getLoginUrl();
}