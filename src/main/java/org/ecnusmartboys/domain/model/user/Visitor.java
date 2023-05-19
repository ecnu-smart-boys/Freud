package org.ecnusmartboys.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Visitor extends User{
    private String emergencyContactName;
    private String emergencyContactPhone;
}
