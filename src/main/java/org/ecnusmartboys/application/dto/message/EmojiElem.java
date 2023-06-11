package org.ecnusmartboys.application.dto.message;

import lombok.Data;

@Data
public class EmojiElem {

    public static final String TYPE = "TIMEmojiElem";

    private int Index;
    private String Data;
    private String Description;
}
