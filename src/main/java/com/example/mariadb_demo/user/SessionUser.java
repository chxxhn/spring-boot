package com.example.mariadb_demo.user;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;

    public SessionUser(Long id, String name, String email) {
    }
}

