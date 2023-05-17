package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.infrastructure.model.mysql.Staff;

import java.time.Duration;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantInfo {

    private Long id;

    private String username;

    private String name;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String phone;

    private String email;

    private Boolean disabled;

    private Integer consultNum;

    private Long accumulatedTime;

    private List<String> supervisors;

    private Staff staff;

    // TODO 排班
}
