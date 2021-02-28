package com.excenit.sso.service;

import com.excenit.sso.entity.SsoUser;
import com.haulmont.cuba.security.entity.User;

public interface MicrosoftRegistrationService {
    String NAME = "sso_MicrosoftRegistrationService";

    SsoUser findOrRegisterUser(String microsoftId, String email, String name);

    User findOrRegisterUser(String userJson);
}