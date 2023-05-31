package org.ecnusmartboys.infrastructure.data.mysql.intermidium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangementInfo {

    private Integer day;

    private String role;

    private Integer total;
}
