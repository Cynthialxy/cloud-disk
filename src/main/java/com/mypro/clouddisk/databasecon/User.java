package com.mypro.clouddisk.databasecon;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class User {
    String name;
    String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    }

