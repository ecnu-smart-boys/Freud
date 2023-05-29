package org.ecnusmartboys.domain.model.user;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ecnusmartboys.domain.model.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;
    private String name;
    private String password;
    private String phone;
    private Integer gender;
    private String email;
    private String avatar;
    private String openID;
    private int age;
    private boolean disabled;
    private String role;
}
