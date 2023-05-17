package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.infrastructure.model.mysql.Visitor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorInfo {

    private Long id;

    private String username;

    private String name;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String phone;

    private String email;

    private Boolean disabled;

    private Visitor visitor;
}
