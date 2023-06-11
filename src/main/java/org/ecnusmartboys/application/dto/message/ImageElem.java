package org.ecnusmartboys.application.dto.message;

import lombok.Data;

import java.util.List;

@Data
public class ImageElem {

    public static final String TYPE = "TIMImageElem";

    private String UUID;
    private int ImageFormat;
    private List<ImageInfo> ImageInfoArray;
}
