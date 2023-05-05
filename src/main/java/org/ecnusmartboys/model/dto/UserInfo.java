package org.ecnusmartboys.model.dto;

import lombok.Data;
import org.ecnusmartboys.model.entity.Staff;
import org.ecnusmartboys.model.entity.Visitor;

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

    private Visitor visitor;

    private Staff staff;
}
