package org.ecnusmartboys.domain.model.arrangement;

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
