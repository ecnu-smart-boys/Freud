package org.ecnusmartboys.domain.model.user;

import lombok.Data;

@Data
public class  Consultant extends User{
    public static final String ROLE = "consultant";

    private String idNumber;

    private String department;

    private String title;

    private Integer arrangement;
}
