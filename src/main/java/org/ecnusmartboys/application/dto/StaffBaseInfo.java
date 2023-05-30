package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffBaseInfo {

    private String id;

    private String name;

    private String avatar = "";
}
