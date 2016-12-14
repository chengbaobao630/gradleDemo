package com.example.domain;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by cheng on 2016/11/10 0010.
 */
public class User {
    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
