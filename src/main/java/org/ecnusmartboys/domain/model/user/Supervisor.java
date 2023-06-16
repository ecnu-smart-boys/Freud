package org.ecnusmartboys.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Supervisor extends User {
    public static final String ROLE = "supervisor";
    private String role = ROLE;

    private String idNumber;

    private String department;

    private String title;

    private String qualification;

    private String qualificationCode;

    private Integer arrangement;

    private Integer maxConversations;
}
