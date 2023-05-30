package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayArrangeInfo {

    private Integer day;

    private Integer supervisors;

    private Integer consultants;
}
