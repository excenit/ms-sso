package com.excenit.sso.entity;

import java.io.Serializable;

public class LoginUserData implements Serializable {
    private String id;
    private String email;
    private String name;

    public LoginUserData(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public LoginUserData(){}



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
