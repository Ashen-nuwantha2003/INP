package com.example.Models;

import java.io.Serializable;

public class ClientData implements Serializable {
    private String username;

    public ClientData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
