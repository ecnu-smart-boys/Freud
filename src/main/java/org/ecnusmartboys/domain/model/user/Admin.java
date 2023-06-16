package org.ecnusmartboys.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    public static final String ROLE = "admin";
    private String role = ROLE;
}
