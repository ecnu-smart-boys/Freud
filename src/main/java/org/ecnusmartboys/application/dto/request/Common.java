package org.ecnusmartboys.application.dto.request;

import lombok.Data;

@Data
public class Common {
    private String userId;
    private String role;
    private Extra extra;
}
