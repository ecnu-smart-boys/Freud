package org.ecnusmartboys.domain.model.user;

import lombok.Data;

@Data
public class Supervisor extends User{
    public static final String ROLE = "supervisor";

    private String idNumber;

    private String department;

    private String title;

    private String qualification;

    private String qualificationCode;

    private Integer arrangement;
}
