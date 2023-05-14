package org.ecnusmartboys.infrastructure.model.wx;

import lombok.Data;

@Data
public class PhoneNumberResponse {

    private String phoneNumber;

    private String purePhoneNumber;

    private String countryCode;

    private Watermark watermark;

    @Data
    public static class Watermark {

        private Long timestamp;

        private String appid;
    }
}
