package org.ecnusmartboys.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Visitor extends User {
    public static final String ROLE = "visitor";
    private String role = ROLE;
    private String emergencyContact;
    private String emergencyPhone;

}
