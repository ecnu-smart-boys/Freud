package org.ecnusmartboys.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Consultant extends User {
    public static final String ROLE = "consultant";
    private String role = ROLE;
    private String idNumber;

    private String department;

    private String title;

    private Integer arrangement;

    private Integer maxConversations;
}
