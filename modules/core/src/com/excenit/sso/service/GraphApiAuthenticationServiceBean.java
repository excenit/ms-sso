package com.excenit.sso.service;

import com.excenit.sso.config.SsoConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


@Service(GraphApiAuthenticationService.NAME)
public class GraphApiAuthenticationServiceBean implements GraphApiAuthenticationService {
    @Inject
    SsoConfig systemConfig;

    private String authorizationCode;
    private String authorizationToken;
    private LocalTime lastRetrievalTime;

    public LocalTime getLastRetrievalTime() {
        return lastRetrievalTime;
    }

    public void setLastRetrievalTime(LocalTime lastRetrievalTime) {
        this.lastRetrievalTime = lastRetrievalTime;
    }


    private final Logger log = LoggerFactory.getLogger(GraphApiAuthenticationServiceBean.class);


    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    @Override
    public String getAuthorizationToken() {

        // Check to see whether token has expired and return the same token if not. Other retrieve a new token
        if ((this.lastRetrievalTime != null) && (this.lastRetrievalTime.plusMinutes(59).isAfter(LocalTime.now()))) {
            return this.authorizationToken;
        }

        //this.setAuthorizationCode(retrieveAuthorizationCode());
        this.authorizationToken = this.retrieveAccessToken();
        this.setLastRetrievalTime(LocalTime.now());

        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }


    private String encodeUrl(String url) {
        String result = url;

        try {
            result = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @return the url used to request for an authorization code which will later be used to retrieve an access token
     */
    @Override
    public String getAuthorizationCodeURL() {

        StringBuffer url = new StringBuffer(systemConfig.getGraphAuthenticationAuthorizeURL());
        url.append("?client_id=");
        url.append(systemConfig.getGraphAuthenticationClientID());
        url.append("&response_type=code");
        url.append("&redirect_uri=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationRedirectURI()));
        url.append("&response_mode=query");
        url.append("&scope=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationScope()));
        url.append("&state=12345");

        log.info(url.toString());
        return url.toString();
    }

    /**
     * @param code
     * @return the url used to retrieve the access token given the authorization code above
     * This is used mainly for the single sign on integration with cuba
     */
    @Override
    public String getAuthorizationTokenURL(String code) {
        StringBuffer url = new StringBuffer(systemConfig.getGraphAuthenticationTokenURL());
        url.append("?client_id=");
        url.append(systemConfig.getGraphAuthenticationClientID());
        url.append("&scope=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationScope()));
        url.append("&code=");
        url.append(code);
        url.append("&redirect_uri=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationRedirectURI()));
        url.append("&grant_type=authorization_code");
        url.append("&client_secret=");
        url.append(systemConfig.getGraphAuthenticationSecret());

        return url.toString();

    }

    public String getAlternateAuthorizationTokenURL(String code) {
        //log.info("Authorization Code : "+code);

        StringBuffer url = new StringBuffer(systemConfig.getAlternateGraphAuthenticationTokenURL());
        url.append("?client_id=");
        url.append(systemConfig.getGraphAuthenticationClientID());
        url.append("&scope=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationScope()));
        url.append("&code=");
        url.append(code);
        url.append("&redirect_uri=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationRedirectURI()));
        url.append("&grant_type=authorization_code");
        url.append("&client_secret=");
        url.append(systemConfig.getGraphAuthenticationSecret());

        return url.toString();

    }

    /**
     * @param refreshToken
     * @return the url to be used to obtain the access token from the refresh token
     * This is mainly used for the single sign on integration with cuba
     */
    public String getNewTokenFromRefreshTokenURL(String refreshToken) {
        StringBuffer url = new StringBuffer(systemConfig.getGraphAuthenticationTokenURL());
        url.append("?client_id=");
        url.append(systemConfig.getGraphAuthenticationClientID());
        url.append("&scope=");
        url.append(encodeUrl(systemConfig.getGraphAuthenticationScope()));
        url.append("&refresh_token=");
        url.append(refreshToken);
        url.append("&grant_type=refresh_token");
        url.append("&client_secret=");
        url.append(systemConfig.getGraphAuthenticationSecret());

        return url.toString();

    }

    /**
     * @return the url used to retrieve access token for the entire application
     * This is NOT user specific access code
     */

    private String getAuthorizationTokenURL() {
        StringBuffer url = new StringBuffer(systemConfig.getGraphAuthenticationTokenURL());
        url.append("?client_id=");
        url.append(systemConfig.getGraphAuthenticationClientID());
        url.append("&scope=");
        url.append(encodeUrl(systemConfig.getGraphRootScope()));
        url.append("&grant_type=client_credentials");
        url.append("&client_secret=");
        url.append(systemConfig.getGraphAuthenticationSecret());

        return url.toString();

    }



   /* private String getAuthorizationTokenURL(){
        StringBuffer url = new StringBuffer(BrokersureSystemParameters.MICROSOFT_GRAPH_AUTH_TOKENURL);
        url.append("?client_id=");
        url.append(BrokersureSystemParameters.MICROSOFT_GRAPH_AUTH_CLIENTID);
        url.append("&scope=");
        url.append(encodeUrl(BrokersureSystemParameters.MICROSOFT_GRAPH_AUTH_SCOPE_ROOT));
        url.append("&grant_type=client_credentials");
        url.append("&client_secret=");
        url.append(BrokersureSystemParameters.MICROSOFT_GRAPH_AUTH_SECRET);

        return url.toString();

    }*/

    /**
     * @return the url used to retrieve an authorization code which will intern be used to retrieve an access token
     * This method is currently not in use
     */
    public String retrieveAuthorizationCode() {

        String response = "";
        String authCode = null;

        String requestMethod = "GET";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        response = postRequestToRestService(requestMethod, header, getAuthorizationCodeURL(), null);
        //log.info(response);

        if (response.contains("code")) {
            authCode = convertJson(response).get("code");
        }
        return authCode;
    }

    @Override
    public String retrieveAccessToken(String code, boolean returnRawJson) {
        if (returnRawJson) {
            return retrieveAccessToken(code);
        }
        String rawJson = retrieveAccessToken(code);
        return convertJson(rawJson).get("access_token");
    }

    /**
     * @return the main application access token which is user independent
     */
    public String retrieveAccessToken() {

        String response = "";
        String accessToken = null;
        String query = null;

        String requestMethod = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        try {
            query = new URL(getAuthorizationTokenURL()).getQuery();
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }

        response = postRequestToRestService(requestMethod, header, getAuthorizationTokenURL(), query.getBytes());
        //log.info(response);

        if (response.contains("access_token")) {
            accessToken = convertJson(response).get("access_token");
        }
        return accessToken;

    }

    /**
     * @param code
     * @return the raw json containing both the access token and the refresh token obtained from this authorization code
     */
    @Override
    public String retrieveAccessToken(String code) {

        String response = "";
        String query = null;

        String requestMethod = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        try {
            query = new URL(getAuthorizationTokenURL(code)).getQuery();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        response = postRequestToRestService(requestMethod, header, getAuthorizationTokenURL(code), query.getBytes());

        if (response.contains("AADSTS700005")) {
            response = postRequestToRestService(requestMethod, header, getAlternateAuthorizationTokenURL(code), query.getBytes());
            log.info(getAlternateAuthorizationTokenURL(code));
        }

        log.info(response);

        return response;
    }

    @Override
    public String retrieveAccessTokenFromRefreshToken(String refreshToken) {

        String response = "";
        String query = null;

        String requestMethod = "POST";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        try {
            query = new URL(getNewTokenFromRefreshTokenURL(refreshToken)).getQuery();
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }

        response = postRequestToRestService(requestMethod, header, getNewTokenFromRefreshTokenURL(refreshToken), query.getBytes());

        // log.info(response);

        return response;
    }


    public Map<String, String> convertJson(String json) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            resultMap = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return resultMap;
    }

    public Map<String, String> getQueryParameters(String url) {
        String query = url.split("\\?")[1];
        Map<String, String> parameterMap = new HashMap<String, String>();


        for (String parameter : query.split("&")) {
            String[] pair = parameter.split("=");
            if (pair.length > 1) {
                parameterMap.put(pair[0], pair[1]);
            }
        }

        return parameterMap;
    }

    public String extractAuthorizationCode(String xmlOutput) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String authCode = null;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlOutput.getBytes()));
            Element input = document.getElementById("input");
            authCode = input.getAttribute("code");
        } catch (Exception e) {
            log.error("Error", e);
        }

        return authCode;

    }

    @Override
    public String postRequestToRestService(String requestMethod, Map<String, String> headers, String apiUrl, byte[] payload) {
        StringBuffer response = new StringBuffer();
        OutputStream os = null;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(requestMethod);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }


            if (payload != null) {
                os = conn.getOutputStream();
                os.write(payload);
                os.flush();
            }


            int responseCode = conn.getResponseCode();

            BufferedReader br;

            if (responseCode <= 300) {
                br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String output = "";

            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            if (response.toString().equals("")) {
                response.append(conn.getResponseMessage());
            }

            conn.disconnect();

        } catch (Exception e) {


            log.error("Error", e);
        }

        return response.toString();

    }
}