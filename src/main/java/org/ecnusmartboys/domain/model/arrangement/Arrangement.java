package org.ecnusmartboys.domain.model.arrangement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Arrangement {

    private String userId;

    private Date date;
}
