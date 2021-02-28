package com.excenit.sso.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.UuidTypeFactory;

import java.util.UUID;

@Source(type = SourceType.DATABASE)
public interface SsoConfig extends Config {
    @Property("microsoft.graph.auth.clientID")
    @Default("75679f55-b61c-4be1-8c83-e0b79f40de7c")
    String getGraphAuthenticationClientID();
    void setGraphAuthenticationClientID(String value);

    @Property("microsoft.graph.auth.secret")
    @Default("t7ajOWFfw[mVy5-50qZEergfMTCZqP_-")
    String getGraphAuthenticationSecret();

    @Property("microsoft.graph.auth.tenantID")
    @Default("a841281e-23d8-403e-99fb-91a985686cfd")
    String getGraphAuthenticationTenantID();

    @Property("microsoft.graph.auth.scope")
    @Default("https://graph.microsoft.com/User.Read openid offline_access https://graph.microsoft.com/Files.Read.All")
    String getGraphAuthenticationScope();

    @Property("microsoft.graph.auth.scope.root")
    @Default("https://graph.microsoft.com/.default")
    String getGraphRootScope();

    @Property("microsoft.graph.documents.baseURL")
    @Default("https://graph.microsoft.com/v1.0/")
    String getGraphDocumentBase();

    @Property("microsoft.graph.documents.previewUrl")
    @Default("sites/excenit2.sharepoint.com/drive/items/")
    String getGraphDocumentPreviewUrl();

    @Property("excenit.sso.config.defaultRoleName")
    @Default("DefaultRole")
    String getDefaultRoleName();

    @Property("excenit.sso.config.defaultApplicationUser")
    @Default("kampaabeng@excenit.com")
    String getDefaultApplicationUser();

    @Property("excenit.sso.config.accountReceivableUser")
    @Default("kampaabeng@excenit.com")
    String getAccountReceivableUser();

    @Property("microsoft.graph.auth.endpoint")
    @Default("https://login.microsoftonline.com/common/oauth2/authorize")
    String getGraphAuthenticationEndpoint();

    @Property("microsoft.graph.auth.redirectURI")
    @Default("http://localhost:8080/app")
    String getGraphAuthenticationRedirectURI();

    @Property("microsoft.graph.auth.authorizeURL")
    @Default("https://login.microsoftonline.com/a841281e-23d8-403e-99fb-91a985686cfd/oauth2/v2.0/authorize")
    String getGraphAuthenticationAuthorizeURL();

    @Property("microsoft.graph.auth.tokenURL")
    @Default("https://login.microsoftonline.com/a841281e-23d8-403e-99fb-91a985686cfd/oauth2/v2.0/token")
    String getGraphAuthenticationTokenURL();

    @Property("microsoft.graph.auth.tokenURL.alt")
    String getAlternateGraphAuthenticationTokenURL();

    @Property("microsoft.graph.api.baseURL")
    @Default("https://graph.microsoft.com/v1.0/")
    String getGraphApiBaseURL();

    @Property("microsoft.graph.api.resource")
    @Default("https://graph.microsoft.com")
    String getGraphResource();

    @Default("0fa2b1a5-1d68-4d69-9fbd-dff348347f93")
    @Property("social.defaultGroupId")
    @Factory(factory = UuidTypeFactory.class)
    UUID getDefaultGroupId();
}