package org.ecnusmartboys.application.dto.message;

import lombok.Data;

@Data
public class SoundElem {

    public static final String TYPE = "TIMSoundElem";

    private String Url;
    private int Size;
    private String UUID;
    private int Second;
    private int Download_Flag;
}
