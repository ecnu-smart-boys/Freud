package org.ecnusmartboys.application.dto;

import lombok.Data;

@Data
public class UserInfo {

    private String id;

    private String username;

    private String name;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String phone;

    private String email;

    private Boolean disabled;

    private String role;
}
