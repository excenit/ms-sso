package com.excenit.sso.service;

import java.util.Map;


public interface GraphApiAuthenticationService {
    String NAME = "sso_GraphApiAuthenticationService";

    String getAuthorizationToken();

    String getAuthorizationCodeURL();

    String getAuthorizationTokenURL(String code);

    String retrieveAccessToken(String code, boolean returnRawJson);

    String retrieveAccessToken(String code);

    String retrieveAccessTokenFromRefreshToken(String refreshToken);

    String postRequestToRestService(String requestMethod, Map<String, String> headers, String apiUrl, byte[] payload);
}