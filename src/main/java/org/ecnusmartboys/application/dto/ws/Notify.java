package org.ecnusmartboys.application.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notify {

    private String type;

    private Object content;
}
