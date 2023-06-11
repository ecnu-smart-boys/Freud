package org.ecnusmartboys.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextElem {

    public static final String TYPE = "TIMTextElem";

    private String Text;
}
