package com.excenit.sso.entity;

import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "sso_SsoUser")
@Extends(User.class)
public class SsoUser extends User {
    private static final long serialVersionUID = -8339962758414661408L;

    @Column(name = "MICROSOFT_ID")
    private String microsoftId;

    public String getMicrosoftId() {
        return microsoftId;
    }

    public void setMicrosoftId(String microsoftId) {
        this.microsoftId = microsoftId;
    }
}