package org.ecnusmartboys.application.dto;

import lombok.Data;
import org.ecnusmartboys.infrastructure.data.mysql.StaffInfo;
import org.ecnusmartboys.infrastructure.data.mysql.VisitorInfo;

import java.util.List;

@Data
public class UserInfo {

    private Long id;

    private String username;

    private String name;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String phone;

    private String email;

    private Boolean disabled;

    private List<String> roles;

    private VisitorInfo visitorInfo;

    private StaffInfo staffInfo;
}
