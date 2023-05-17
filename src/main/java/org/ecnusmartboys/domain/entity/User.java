package org.ecnusmartboys.domain.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity{
    private String name;
    private String password;
    private String phone;
    private String gender;
    private String email;
    private String avatar;
    private String openID;
    private int age;
    private boolean disabled;
}
