package org.ecnusmartboys.application.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OnlineState implements BaseEnum{
    OFFLINE(0, "离线"),
    IDLE(1, "空闲"),
    IN_CONVERSATION(2, "会话中");

    private final int value;

    private final String description;

    @Override
    public String getTextValue() {
        return String.valueOf(value);
    }

    @Override
    public String getDescription() {
        return description;
    }
}
